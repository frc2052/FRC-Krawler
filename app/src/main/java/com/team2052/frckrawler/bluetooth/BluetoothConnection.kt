package com.team2052.frckrawler.bluetooth

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import java.io.IOException

private val TAG = BluetoothCapabilities::class.simpleName
private const val BUFFER_SIZE = 1024

class BluetoothConnection(private val connectedDevice: BluetoothDevice, private val bluetoothSocket: BluetoothSocket)
{

    fun closeConnection()
    {
        bluetoothSocket?.close()
    }

    private inner class ConnectionThread(private val bluetoothSocket: BluetoothSocket) : Thread() {
        private val inputStream = bluetoothSocket.inputStream
        private val outputStream = bluetoothSocket.outputStream
        private val buffer = ByteArray(BUFFER_SIZE)

        override fun run() {

        }

        fun write(bytes: ByteArray) {
            try {
                outputStream.write(bytes)
            } catch (e: IOException) {
                Log.e(TAG, "Error occurred while sending data", e)
            }
        }

        fun close() {

        }
    }
}