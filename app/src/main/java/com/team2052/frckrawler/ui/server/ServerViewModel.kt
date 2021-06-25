package com.team2052.frckrawler.ui.server

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.team2052.frckrawler.bluetooth.BluetoothController
import javax.inject.Inject

class ServerViewModel @Inject constructor(
    //bluetoothController: BluetoothController
) : ViewModel() {

    private val serverState = mutableStateOf(false)

    fun serverRunning() = serverState.value

    fun startServer() {
        serverState.value = true
    }

    fun stopServer() {
        serverState.value = false
    }

}