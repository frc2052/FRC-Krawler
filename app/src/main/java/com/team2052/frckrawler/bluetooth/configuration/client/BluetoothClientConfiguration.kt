package com.team2052.frckrawler.bluetooth.configuration.client

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import com.team2052.frckrawler.bluetooth.BluetoothManager
import com.team2052.frckrawler.bluetooth.configuration.BluetoothConfiguration

private val TAG = BluetoothClientConfiguration::class.simpleName

class BluetoothClientConfiguration() : BluetoothConfiguration() {

    private lateinit var bondedDevices: Set<BluetoothDevice>
    private var connectedDevice: BluetoothDevice? = null

    private lateinit var clientThread: ClientThread

    override fun <T : BluetoothConfiguration>initialize(bluetoothAdapter: BluetoothAdapter): T {
        if(!initialized) {
            bondedDevices = bluetoothAdapter.bondedDevices
            for(device in bondedDevices) {
                if(device.name == BluetoothManager.BluetoothConstants.SERVICE_NAME) {
                    connectedDevice = device
                    break
                }
            }
            if(connectedDevice != null) {
                clientThread = ClientThread(connectedDevice!!)
            }
        }
        return super.initialize(bluetoothAdapter)
    }

    override fun run(context: Context) {
        if(connectedDevice == null) {
            val intentFilter = IntentFilter().apply {
                this.addAction(BluetoothDevice.ACTION_FOUND)
            }
            context.registerReceiver(bluetoothDeviceReceiver, intentFilter)
            bluetoothAdapter?.startDiscovery()
            Log.d(TAG, "starting discovery ")
        }
    }

    private val bluetoothDeviceReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when(intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    if(device?.name == BluetoothManager.BluetoothConstants.SERVICE_NAME) {
                        connectedDevice = device
                        bluetoothAdapter?.cancelDiscovery()
                        context.unregisterReceiver(this)
                    }
                    Log.d(TAG, "discovered device: " + device?.name)
                }
            }
        }
    }

    fun connect() {

    }

    override fun close(context: Context) {
        if(this::clientThread.isInitialized) {
            clientThread.close()
        }
        bluetoothAdapter?.cancelDiscovery()
        context.unregisterReceiver(bluetoothDeviceReceiver)
        Log.d(TAG, "ended discovery ")
    }

    private inner class ClientThread(device: BluetoothDevice) : Thread() {

        private val clientSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
            device.createRfcommSocketToServiceRecord(java.util.UUID.fromString(BluetoothManager.BluetoothConstants.UUID))
        }

        override fun run() {

        }

        fun close() {

        }
    }
}