package com.team2052.frckrawler.ui.server

import android.content.Context
import android.os.Build
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team2052.frckrawler.bluetooth.BluetoothController
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Delay
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ServerViewModel @Inject constructor(
    private val bluetoothController: BluetoothController
) : ViewModel() {

    val serverState = mutableStateOf(ServerState.DISABLED)

    private val locationTactic = mutableStateOf(LocationTactic.NONE)
    private val locationPermissionGranted = mutableStateOf(false)

    /**
     * 1. State = enabling
     * 2. Request & await permissions
     * 3. Request Bluetooth
     * 4. Start server thread
     * 5. State = enabled
     */
    suspend fun startServer(
        requestLocationPermission: suspend () -> Boolean,
        requestBluetooth: suspend () -> Boolean,
    ) {
        serverState.value = ServerState.ENABLING

        // Select & request location strategies
        when {
            Build.VERSION.SDK_INT <= 22 -> {
                locationTactic.value = LocationTactic.NONE
                locationPermissionGranted.value = true
            }
            Build.VERSION.SDK_INT >= 26 -> {
                locationTactic.value = LocationTactic.COMPANION_DEVICE
                locationPermissionGranted.value = true
            }
            else -> {
                locationTactic.value = LocationTactic.COARSE_LOCATION
                locationPermissionGranted.value = requestLocationPermission()
            }
        }

        // Check location strategy worked
        if (!locationPermissionGranted.value) {
            serverState.value = ServerState.DISABLED
            return
        }

        // Request bluetooth if not all ready enabled
        if (!bluetoothController.bluetoothEnabled()) {
            if (!requestBluetooth()) {
                serverState.value = ServerState.DISABLED
                return
            } else {
                bluetoothController.enableBluetooth()
            }
        }

        delay(1000)

        serverState.value = ServerState.ENABLED
    }

    fun stopServer() {
        serverState.value = ServerState.DISABLING

        serverState.value = ServerState.DISABLED
    }

//    var isRefreshing by mutableStateOf(false)
//    fun refresh() {
//        isRefreshing = true
//        viewModelScope.launch {
//            delay(1000)
//            isRefreshing = false
//        }
//    }
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

enum class ServerState {
    ENABLED,
    ENABLING,
    DISABLED,
    DISABLING,
}

enum class LocationTactic {
    NONE,
    COARSE_LOCATION,
    COMPANION_DEVICE,
}