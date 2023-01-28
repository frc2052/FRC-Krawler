package com.team2052.frckrawler.ui.server

import android.bluetooth.BluetoothAdapter
import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.team2052.frckrawler.bluetooth.server.SyncServiceController
import com.team2052.frckrawler.ui.permissions.PermissionManager
import com.team2052.frckrawler.ui.permissions.RequiredPermissions
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ServerViewModel @Inject constructor(
    private val bluetoothAdapter: BluetoothAdapter,
    private val permissionManager: PermissionManager,
    private val syncServiceController: SyncServiceController
) : ViewModel() {

    var serverState by mutableStateOf(ServerState.DISABLED)
    var showPermissionRequests by mutableStateOf(false)
    var requestEnableBluetooth by mutableStateOf(false)
    var serverConfiguration by mutableStateOf(ServerConfiguration(null, null))

    private val bluetoothStrategy = getBluetoothStrategy()

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
    }

    fun stopServer() {
        serverState = ServerState.DISABLING

        syncServiceController.stopServer()

        serverState = ServerState.DISABLED
    }


//
//    val scouts: MutableList<Scout> = mutableListOf()
//
//    init {
//        bluetoothController.bondedDevices().forEach { scouts.add(Scout(it)) }
//    }
//
//    private val serverState = mutableStateOf(false)
//
//    fun serverRunning() = serverState.value
//
//    /**
//     * 1. Change server state to running
//     * 2. Enable Bluetooth
//     * 3. Begin monitoring bluetooth state changes through bluetooth controller events.
//     */
//    fun startServer() {
//        serverState.value = true
//        bluetoothController.toggleBluetooth(true)
//        bluetoothController.makeDiscoverable(20)
////        bluetoothController.startDiscovery {
////            scouts.plus(Scout(it))
////        }
//    }
//
//    /**
//     * 1. Change server state to down
//     * 2. Notify scouts
//     * 3. Cancel any bluetooth services running
//     */
//    fun stopServer() {
//        serverState.value = false
//        bluetoothController.cancelDiscovery()
//    }
//
//    data class Scout(
//        val device: BluetoothDevice
//    )
}

private fun getBluetoothStrategy(): BluetoothStrategy {
    return when {
        Build.VERSION.SDK_INT >= 26 -> BluetoothStrategy.COMPANION_DEVICE
        else -> BluetoothStrategy.COARSE_LOCATION
    }
}

enum class BluetoothStrategy {
    COARSE_LOCATION,
    COMPANION_DEVICE,
}