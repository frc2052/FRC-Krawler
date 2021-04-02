package com.team2052.frckrawler.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log

private val TAG = BluetoothDiscoverer::class.simpleName

class BluetoothDiscoverer(
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter(),
    private val maxDiscoverable: Int = 0
) {

    private lateinit var context: Context

    private var deviceList: List<BluetoothDevice>? = bluetoothAdapter?.bondedDevices?.toList()
    private val deviceDiscoveredIntentFilter = IntentFilter().apply {
        addAction(BluetoothDevice.ACTION_FOUND)
    }

    fun startDiscovery(context: Context) {
        this.context = context
        this.context.registerReceiver(deviceDiscoveredReceiver, deviceDiscoveredIntentFilter)
        bluetoothAdapter?.startDiscovery()
        Log.i(TAG, "Bluetooth discovery started. Current devices:");
        deviceList?.forEach {
            Log.i(TAG, " - ${it.name}")
        }
    }

    fun endDiscovery() {
        this.context.unregisterReceiver(deviceDiscoveredReceiver)
        bluetoothAdapter?.cancelDiscovery()
        Log.i(TAG, "Bluetooth discovery ended")
    }

    fun getDiscoveredDevices(): List<BluetoothDevice>? = deviceList

    private val deviceDiscoveredReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when(intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
//                    if(maxDiscoverable > 0 && deviceList!!.size < maxDiscoverable) {
                        val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                        deviceList!!.plusElement(device)
                        Log.i(TAG, "Bluetooth device discovered with name ${device?.name}")
                        Log.i(TAG, "New device list: ${deviceList.toString()}")
//                    } else {
//                        Log.i(TAG, "nope")
//                    }
                }
            }
        }
    }
}