package com.team2052.frckrawler.ui.scout

import android.bluetooth.BluetoothAdapter
import androidx.activity.ComponentActivity
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team2052.frckrawler.bluetooth.client.ServerConnectionManager
import com.team2052.frckrawler.bluetooth.client.ServerConnectionResult
import com.team2052.frckrawler.ui.navigation.Screen
import com.team2052.frckrawler.ui.permissions.PermissionManager
import com.team2052.frckrawler.ui.permissions.RequiredPermissions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScoutViewModel @Inject constructor(
    private val bluetoothAdapter: BluetoothAdapter,
    private val permissionManager: PermissionManager,
    private val serverManager: ServerConnectionManager
) : ViewModel() {

    var currentTab by mutableStateOf(Screen.ScoutHome)
    var showPermissionRequests by mutableStateOf(false)
    var requestEnableBluetooth by mutableStateOf(false)

    var serverConnectionState: ServerConnectionState by mutableStateOf(ServerConnectionState.NotConnected)

    fun connectToServer(activity: ComponentActivity) {
        // Check location strategy worked
        if (!permissionManager.hasPermissions(RequiredPermissions.serverPermissions)) {
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
                is ServerConnectionResult.ServerConnected -> ServerConnectionState.Connected(
                    name = connectionResult.server.name
                )
            }
        }
    }

}