package com.team2052.frckrawler.bluetooth

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.Closeable
import javax.inject.Inject

// TODO: Implement hilt
class BluetoothController @Inject constructor(
    private val activity: Context,
    private val bluetooth: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter(),
) : Closeable {

    /**
     * STATE SHOULD NEVER BE CHANGED WITHOUT USER CONSENT!
     */
    fun toggleBluetooth(state: Boolean) { if (bluetooth != null) { if (state) bluetooth.enable() else bluetooth.disable() } }

    @Synchronized fun startDiscovery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: This might not be working
                ActivityCompat.requestPermissions(activity as Activity, arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION), 1)
            }
        }
        cancelDiscovery()
        if(bluetooth != null && !bluetooth.startDiscovery()) {
            Toast.makeText(activity, "Failed to start bluetooth discovery!", Toast.LENGTH_LONG).show()
        }
    }

    fun cancelDiscovery() = if (bluetooth != null && bluetooth.isDiscovering) { bluetooth.cancelDiscovery() } else null

    @Synchronized fun pair(device: BluetoothDevice) : Boolean {
        cancelDiscovery()
        return device.createBond()
    }
    fun paired(device: BluetoothDevice) : Boolean = bluetooth?.bondedDevices?.contains(device) ?: false

    private val bluetoothBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                BluetoothDevice.ACTION_FOUND -> {

                }
                BluetoothDevice.ACTION_BOND_STATE_CHANGED -> {
                    val state = bluetooth?.state ?: BluetoothAdapter.ERROR
                }
            }
        }
    }

    override fun close() {
        //bluetoothBroadcastReceiver.close()
    }
}