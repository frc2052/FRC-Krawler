package com.team2052.frckrawler.ui.scout

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import androidx.activity.ComponentActivity
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.team2052.frckrawler.bluetooth.client.ScoutSyncWorker
import com.team2052.frckrawler.bluetooth.client.ServerConnectionManager
import com.team2052.frckrawler.bluetooth.client.ServerConnectionResult
import com.team2052.frckrawler.ui.navigation.Screen
import com.team2052.frckrawler.ui.permissions.PermissionManager
import com.team2052.frckrawler.ui.permissions.RequiredPermissions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ScoutViewModel @Inject constructor(
    private val bluetoothAdapter: BluetoothAdapter,
    private val permissionManager: PermissionManager,
    private val serverManager: ServerConnectionManager,
    private val workManager: WorkManager,
) : ViewModel() {

    var showPermissionRequests by mutableStateOf(false)
    var requestEnableBluetooth by mutableStateOf(false)

    var serverConnectionState: ServerConnectionState by mutableStateOf(ServerConnectionState.NotConnected)
    var server: BluetoothDevice? = null


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
        }
    }

    fun performSync() {
        Timber.tag("client").d("queuing sync: $server")
        // TODO come back and clean up
        server?.let {
            val workRequest = OneTimeWorkRequestBuilder<ScoutSyncWorker>()
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .setInputData(workDataOf(
                    ScoutSyncWorker.DATA_SERVER_ADDRESS to it.address
                ))
                .build()
            workManager.enqueue(workRequest)
        }
    }

}