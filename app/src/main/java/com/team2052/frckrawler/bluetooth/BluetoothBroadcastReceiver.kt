package com.team2052.frckrawler.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import java.io.Closeable

class BluetoothBroadcastReceiver(
    val context: Context,
    val bluetooth: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter(),
    var onDeviceDiscovered: (BluetoothDevice) -> Unit = { },
    var onDeviceDiscoveryFinished: () -> Unit = { },
    var onBluetoothStateChange: (Int) -> Unit = { },
    var onDevicePairingStateChange: () -> Unit = { }
) : BroadcastReceiver(), Closeable {

    init {
        val filter = IntentFilter()
        filter.addAction(BluetoothDevice.ACTION_FOUND)
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        context.registerReceiver(this, filter)
    }

    override fun onReceive(context: Context, intent: Intent) {
        when(intent.action!!) {
            BluetoothDevice.ACTION_FOUND ->                 onDeviceDiscovered(intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)!!)
            BluetoothAdapter.ACTION_DISCOVERY_FINISHED ->   onDeviceDiscoveryFinished()
            BluetoothAdapter.ACTION_STATE_CHANGED ->        onBluetoothStateChange(bluetooth?.state ?: BluetoothAdapter.ERROR)
            BluetoothDevice.ACTION_BOND_STATE_CHANGED ->    onDevicePairingStateChange()
        }
    }

    override fun close() {
        context.unregisterReceiver(this)
    }
}