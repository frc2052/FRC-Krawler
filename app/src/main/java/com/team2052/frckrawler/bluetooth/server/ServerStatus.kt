package com.team2052.frckrawler.bluetooth.server

import android.bluetooth.BluetoothDevice

data class ServerStatus(val state: Boolean = false, val syncing: Boolean = false, val device: BluetoothDevice? = null) {
    companion object {
        val off = ServerStatus()
    }
}
