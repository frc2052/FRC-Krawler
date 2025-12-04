@file:OptIn(ExperimentalCoroutinesApi::class)

package com.team2052.frckrawler.ui.scout.remote

import android.annotation.SuppressLint
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
import com.team2052.frckrawler.bluetooth.client.getFailureReason
import com.team2052.frckrawler.data.local.Game
import com.team2052.frckrawler.data.local.GameDao
import com.team2052.frckrawler.data.local.MetricDao
import com.team2052.frckrawler.data.local.MetricDatumDao
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import dev.zacsweers.metro.AppScope
import com.team2052.frckrawler.ui.permissions.PermissionManager
import com.team2052.frckrawler.ui.permissions.RequiredPermissions
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Optional
import java.util.UUID

@ContributesIntoMap(AppScope::class)
@ViewModelKey(RemoteScoutViewModel::class)
@Inject
class RemoteScoutViewModel(
  bluetoothAdapterOptional: Optional<BluetoothAdapter>,
  private val permissionManager: PermissionManager,
  private val serverManager: ServerConnectionManager,
  private val workManager: WorkManager,
  metricDatumDao: MetricDatumDao,
  metricDao: MetricDao,
  gameDao: GameDao,
) : ViewModel() {

  companion object {
    private const val SYNC_WORK_NAME = "remote_scout_sync"
  }

  private val bluetoothAdapter = bluetoothAdapterOptional.get()

  var showPermissionRequests by mutableStateOf(false)
  var requestEnableBluetooth by mutableStateOf(false)

  var serverConnectionState: ServerConnectionState by mutableStateOf(ServerConnectionState.NotConnected)
  var server: BluetoothDevice? = null

  private val lastSyncId: MutableStateFlow<UUID?> = MutableStateFlow(null)

  private val pendingMetrics = metricDatumDao.getRemoteScoutDataFlow()
  private val syncWork = workManager.getWorkInfosForUniqueWorkFlow(SYNC_WORK_NAME)
  val syncState: StateFlow<ServerSyncState> = combine(
    syncWork,
    pendingMetrics,
    lastSyncId,
  ) { workInfos, pendingMetrics, lastSyncId ->
    val lastSyncTime = getLastSyncTime(workInfos)
    val lastSyncInfo = workInfos.firstOrNull { it.id == lastSyncId }
    val lastSyncFailed = lastSyncInfo != null && lastSyncInfo.state == WorkInfo.State.FAILED
    val failureReason = lastSyncInfo?.outputData?.getFailureReason()
    when {
      lastSyncId == null -> ServerSyncState.NotSynced(hasSyncFailure = false)
      workInfos.any { it.isWaitingOrRunning() } -> ServerSyncState.Syncing
      else -> {
        if (lastSyncTime == null) {
          ServerSyncState.NotSynced(
            hasSyncFailure = lastSyncFailed,
            failureReason = failureReason,
          )
        } else {
          ServerSyncState.Synced(
            pendingDataCount = pendingMetrics.size,
            lastSyncTime = lastSyncTime,
            hasSyncFailure = lastSyncFailed
          )
        }
      }
    }
  }.stateIn(
    viewModelScope,
    started = SharingStarted.WhileSubscribed(5_000),
    initialValue = ServerSyncState.NotSynced(hasSyncFailure = false)
  )

  private val gameFlow = gameDao.getWithUpdates(Game.SCOUT_GAME_ID)
  private val matchMetricsCountFlow: Flow<Int> = gameFlow
    .flatMapLatest { game ->
      if (game.matchMetricsSetId != null) {
        metricDao.getMetricCountFlow(game.matchMetricsSetId)
      } else {
        flowOf(0)
      }
    }
  private val pitMetricsCountFlow: Flow<Int> = gameFlow
    .flatMapLatest { game ->
      if (game.pitMetricsSetId != null) {
        metricDao.getMetricCountFlow(game.pitMetricsSetId)
      } else {
        flowOf(0)
      }
    }

  val hasMatchMetrics: StateFlow<Boolean> = combine(
    syncState,
    matchMetricsCountFlow,
  ) { syncState, metricsCount ->
    syncState is ServerSyncState.Synced && metricsCount > 0
  }.stateIn(
    viewModelScope,
    started = SharingStarted.WhileSubscribed(5_000),
    initialValue = false
  )

  val hasPitMetrics: StateFlow<Boolean> = combine(
    syncState,
    pitMetricsCountFlow,
  ) { syncState, metricsCount ->
    syncState is ServerSyncState.Synced && metricsCount > 0
  }.stateIn(
    viewModelScope,
    started = SharingStarted.WhileSubscribed(5_000),
    initialValue = false
  )

  @SuppressLint("MissingPermission")
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
        ExistingWorkPolicy.APPEND_OR_REPLACE,
        workRequest,
      )
      lastSyncId.value = workRequest.id
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