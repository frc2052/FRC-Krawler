package com.team2052.frckrawler.ui.server

import android.bluetooth.BluetoothDevice
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team2052.frckrawler.bluetooth.BluetoothController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ServerViewModel @Inject constructor(
    private val bluetoothController: BluetoothController
) : ViewModel() {

    var isRefreshing by mutableStateOf(false)
    fun refresh() {
        isRefreshing = true
        viewModelScope.launch {
            delay(1000)
            isRefreshing = false
        }
    }

    val scouts: MutableList<Scout> = mutableListOf()

    init {
        bluetoothController.bondedDevices().forEach { scouts.add(Scout(it)) }
    }

    private val serverState = mutableStateOf(false)

    fun serverRunning() = serverState.value

    /**
     * 1. Change server state to running
     * 2. Enable Bluetooth
     * 3. Begin monitoring bluetooth state changes through bluetooth controller events.
     */
    fun startServer() {
        serverState.value = true
        bluetoothController.toggleBluetooth(true)
        bluetoothController.makeDiscoverable(20)
//        bluetoothController.startDiscovery {
//            scouts.plus(Scout(it))
//        }
    }

    /**
     * 1. Change server state to down
     * 2. Notify scouts
     * 3. Cancel any bluetooth services running
     */
    fun stopServer() {
        serverState.value = false
        bluetoothController.cancelDiscovery()
    }

    data class Scout(
        val device: BluetoothDevice
    )
}