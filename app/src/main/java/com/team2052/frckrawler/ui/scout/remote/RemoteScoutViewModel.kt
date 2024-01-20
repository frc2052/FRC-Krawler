package com.team2052.frckrawler.ui.scout.remote

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.team2052.frckrawler.bluetooth.client.ScoutSyncWorker
import com.team2052.frckrawler.bluetooth.client.ServerConnectionManager
import com.team2052.frckrawler.bluetooth.client.ServerConnectionResult
import com.team2052.frckrawler.data.local.MetricDao
import com.team2052.frckrawler.data.local.MetricDatumDao
import com.team2052.frckrawler.data.local.MetricSet
import com.team2052.frckrawler.ui.permissions.PermissionManager
import com.team2052.frckrawler.ui.permissions.RequiredPermissions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Optional
import java.util.UUID
import javax.inject.Inject

// TODO cancel work if disconnected from server
@HiltViewModel
class RemoteScoutViewModel @Inject constructor(
  bluetoothAdapterOptional: Optional<BluetoothAdapter>,
  private val permissionManager: PermissionManager,
  private val serverManager: ServerConnectionManager,
  private val workManager: WorkManager,
  metricDatumDao: MetricDatumDao,
  metricDao: MetricDao,
) : ViewModel() {

  companion object {
    private const val SYNC_WORK_NAME = "remote_scout_sync"
  }

  private val bluetoothAdapter = bluetoothAdapterOptional.get()

  var showPermissionRequests by mutableStateOf(false)
  var requestEnableBluetooth by mutableStateOf(false)

  var serverConnectionState: ServerConnectionState by mutableStateOf(ServerConnectionState.NotConnected)
  var server: BluetoothDevice? = null

  private val hasStartedSync = MutableStateFlow(false)

  private val pendingMetrics = metricDatumDao.getRemoteScoutDataFlow()
  private val syncWork = workManager.getWorkInfosForUniqueWorkFlow(SYNC_WORK_NAME)
  val syncState: StateFlow<ServerSyncState> = combine(
    syncWork,
    pendingMetrics,
    hasStartedSync
  ) { workInfos, pendingMetrics, hasStartedSync ->
    when {
      !hasStartedSync -> ServerSyncState.NotSynced
      workInfos.any { it.isWaitingOrRunning() } -> ServerSyncState.Syncing
      else -> {
        val lastSyncTime = getLastSyncTime(workInfos)
        if (lastSyncTime == null) {
          ServerSyncState.NotSynced
        } else {
          ServerSyncState.Synced(
            pendingDataCount = pendingMetrics.size,
            lastSyncTime = lastSyncTime
          )
        }
      }
    }
  }.stateIn(
    viewModelScope,
    started = SharingStarted.WhileSubscribed(5_000),
    initialValue = ServerSyncState.NotSynced
  )

  val hasMatchMetrics: StateFlow<Boolean> = combine(
    syncState,
    metricDao.getMetricCountFlow(MetricSet.SCOUT_MATCH_METRIC_SET_ID)
  ) { syncState, metricsCount ->
    syncState is ServerSyncState.Synced && metricsCount > 0
  }.stateIn(
    viewModelScope,
    started = SharingStarted.WhileSubscribed(5_000),
    initialValue = false
  )

  val hasPitMetrics: StateFlow<Boolean> = combine(
    syncState,
    metricDao.getMetricCountFlow(MetricSet.SCOUT_PIT_METRIC_SET_ID)
  ) { syncState, metricsCount ->
    syncState is ServerSyncState.Synced && metricsCount > 0
  }.stateIn(
    viewModelScope,
    started = SharingStarted.WhileSubscribed(5_000),
    initialValue = false
  )

  // TODO skip pairing if a server is already paired and on?
  fun connectToServer(activity: ComponentActivity) {
    // Check location strategy worked
    if (!permissionManager.hasPermissions(RequiredPermissions.clientPermissions)) {
      showPermissionRequests = true
      return
    }

    // Request bluetooth if not all ready enabled
    if (!bluetoothAdapter.isEnabled) {
      requestEnableBluetooth = true
      return
    }

    serverConnectionState = ServerConnectionState.Connecting
    viewModelScope.launch {
      val connectionResult = serverManager.connectToNewServer(activity)

      serverConnectionState = when (connectionResult) {
        is ServerConnectionResult.Cancelled -> ServerConnectionState.NotConnected
        is ServerConnectionResult.NoFrcKrawlerServiceFound -> ServerConnectionState.NoFrcKrawlerServiceFound
        is ServerConnectionResult.PairingFailed -> ServerConnectionState.PairingFailed
        is ServerConnectionResult.ServerConnected -> {
          server = connectionResult.server
          ServerConnectionState.Connected(
            name = connectionResult.server.name
          )
        }
      }

      if (serverConnectionState is ServerConnectionState.Connected) {
        performSync()
      }
    }
  }

  fun performSync() {
    Timber.tag("client").d("queuing sync: $server")
    // TODO come back and clean up
    // TODO do we need to cancel work if leaving the app?
    server?.let {
      val workRequest = OneTimeWorkRequestBuilder<ScoutSyncWorker>()
        .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
        .setInputData(
          workDataOf(
            ScoutSyncWorker.DATA_SERVER_ADDRESS to it.address
          )
        )
        .build()
      workManager.enqueueUniqueWork(
        SYNC_WORK_NAME,
        ExistingWorkPolicy.APPEND,
        workRequest,
      )

      hasStartedSync.value = true
    }
  }

  private fun getLastSyncTime(
    workInfos: List<WorkInfo>,
  ): ZonedDateTime? {
    val mostRecentSuccessTime =
      workInfos.filter { it.state == WorkInfo.State.SUCCEEDED }
        .map {
          it.outputData.getLong(ScoutSyncWorker.RESULT_END_TIMESTAMP, 0)
        }
        .filter { it > 0 }
        .maxOrNull()

    return mostRecentSuccessTime?.let {
      ZonedDateTime.ofInstant(
        Instant.ofEpochSecond(mostRecentSuccessTime),
        ZoneId.systemDefault()
      )
    }
  }

  private fun WorkInfo.isWaitingOrRunning() = state == WorkInfo.State.RUNNING
          || state == WorkInfo.State.BLOCKED
          || state == WorkInfo.State.ENQUEUED

}