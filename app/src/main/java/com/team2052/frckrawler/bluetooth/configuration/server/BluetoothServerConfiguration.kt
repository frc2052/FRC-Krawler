package com.team2052.frckrawler.bluetooth.configuration.server

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.util.Log
import com.team2052.frckrawler.bluetooth.BluetoothConnection
import com.team2052.frckrawler.bluetooth.BluetoothManager
import com.team2052.frckrawler.bluetooth.configuration.BluetoothConfiguration
import java.io.IOException
import java.util.*

private val TAG = BluetoothServerConfiguration::class.simpleName

class BluetoothServerConfiguration : BluetoothConfiguration() {

    private var serverThread: ServerThread? = null

    override fun <T : BluetoothConfiguration>initialize(bluetoothAdapter: BluetoothAdapter) : T {
        if(!initialized) {
            serverThread = ServerThread()
            this.initialized = true
        }
        return super.initialize(bluetoothAdapter)
    }

    override fun run(context: Context) {
        serverThread?.start()
        // make discoverable

    }

    override fun close(context: Context) {
        serverThread?.close()
    }

    private inner class ServerThread() : Thread() {

        private val serverSocket: BluetoothServerSocket? by lazy(LazyThreadSafetyMode.NONE) {
            bluetoothAdapter?.listenUsingRfcommWithServiceRecord(BluetoothManager.BluetoothConstants.SERVICE_NAME, UUID.fromString(BluetoothManager.BluetoothConstants.UUID))
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
}