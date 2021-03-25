package com.team2052.frckrawler.bluetooth.configuration.server

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.util.Log
import com.team2052.frckrawler.bluetooth.BluetoothCapabilities
import com.team2052.frckrawler.bluetooth.BluetoothConnection
import com.team2052.frckrawler.bluetooth.BluetoothManager
import java.io.IOException
import java.util.*

private val TAG = BluetoothCapabilities::class.simpleName

class ServerThread(
    private val bluetoothAdapter: BluetoothAdapter,
    private val bluetoothServerConfiguration: BluetoothServerConfiguration
    ) : Thread() {

    private val serverSocket: BluetoothServerSocket? by lazy(LazyThreadSafetyMode.NONE) {
        bluetoothAdapter.listenUsingRfcommWithServiceRecord(BluetoothManager.BluetoothConstants.SERVICE_NAME, UUID.fromString(BluetoothManager.BluetoothConstants.UUID))
    }

    override fun run() {

        Log.i(TAG, "Starting bluetooth server socket...")

        // TODO: only one connection is accepted at the moment, we will need up to 6
        var open = true
        while(open) {
            val clientSocket: BluetoothSocket? = try {
                serverSocket?.accept()
            } catch (e: IOException) {
                Log.e(TAG, "Server socket failed to accept client request", e)
                open = false
                null // return value for clientSocket
            }
            clientSocket?.also {
                val bluetoothConnection = BluetoothConnection(clientSocket.remoteDevice, clientSocket)
                serverSocket?.close()
                open = false
            }
        }
    }

    fun close() {
        try {
            serverSocket?.close()
        } catch(e: IOException) {
            Log.e(TAG, "Failed to prematurely close server socket\nrun away thread!", e)
        }

        join()
    }
}