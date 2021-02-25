package com.team2052.frckrawler.bluetooth

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket

class BluetoothConnection(private val connectedDevice: BluetoothDevice?, private val bluetoothSocket: BluetoothSocket?) {

    fun closeConnection() {
        bluetoothSocket?.close()
    }

}