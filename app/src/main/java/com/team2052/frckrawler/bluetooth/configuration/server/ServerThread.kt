package com.team2052.frckrawler.bluetooth.configuration.server

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import java.util.*

class ServerThread(val bluetoothAdapter: BluetoothAdapter, val bluetoothServerConfiguration: BluetoothServerConfiguration) : Thread()
{
    private val serverSocket: BluetoothServerSocket? by lazy(LazyThreadSafetyMode.NONE)
    {
        bluetoothAdapter.listenUsingRfcommWithServiceRecord(bluetoothServerConfiguration.SERVICE_NAME, UUID.fromString(bluetoothServerConfiguration.UUID))
    }

    override fun run()
    {
        var open = true
        while(open)
        {
            val clientSocket: BluetoothSocket
        }
    }
}