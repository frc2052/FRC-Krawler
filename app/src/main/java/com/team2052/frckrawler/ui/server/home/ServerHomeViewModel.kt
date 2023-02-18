package com.team2052.frckrawler.ui.server.home

import android.bluetooth.BluetoothAdapter
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team2052.frckrawler.bluetooth.server.ConnectedScoutObserver
import com.team2052.frckrawler.bluetooth.server.SyncServiceController
import com.team2052.frckrawler.data.model.RemoteScout
import com.team2052.frckrawler.ui.permissions.PermissionManager
import com.team2052.frckrawler.ui.permissions.RequiredPermissions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ServerHomeViewModel @Inject constructor(
    private val bluetoothAdapter: BluetoothAdapter,
    private val permissionManager: PermissionManager,
    private val syncServiceController: SyncServiceController,
    private val connectedScoutObserver: ConnectedScoutObserver
) : ViewModel() {

    var serverState by mutableStateOf(ServerState.DISABLED)
    var showPermissionRequests by mutableStateOf(false)
    var requestEnableBluetooth by mutableStateOf(false)
    var serverConfiguration by mutableStateOf(ServerConfiguration(null, null))
    var connectedScouts: List<RemoteScout> by mutableStateOf(emptyList())

    /**
     * 1. State = enabling
     * 2. Request & await permissions
     * 3. Request Bluetooth
     * 4. Start server thread
     * 5. State = enabled
     */
    fun startServer() {
        serverState = ServerState.ENABLING

        // Check location strategy worked
        if (!permissionManager.hasPermissions(RequiredPermissions.serverPermissions)) {
            serverState = ServerState.DISABLED
            showPermissionRequests = true
            return
        }

        // Request bluetooth if not all ready enabled
        if (!bluetoothAdapter.isEnabled) {
            serverState = ServerState.DISABLED
            requestEnableBluetooth = true
            return
        }

        syncServiceController.startServer()

        serverState = ServerState.ENABLED

        viewModelScope.launch {
            connectedScoutObserver.devices.collectLatest {
                connectedScouts = it
            }
        }
    }

    fun stopServer() {
        serverState = ServerState.DISABLING

        syncServiceController.stopServer()

        serverState = ServerState.DISABLED
    }

}