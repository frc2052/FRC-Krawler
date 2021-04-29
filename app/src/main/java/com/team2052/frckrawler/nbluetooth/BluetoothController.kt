package com.team2052.frckrawler.nbluetooth

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.Flow
import java.io.Closeable

/**
 * Contains vital bluetooth controls and functions
 *
 * @author Matt
 * @since 4/28/2021
 */

class BluetoothController(
    private val activity: Activity,
    private val bluetooth: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter(),
    private val bluetoothBroadcastReceiver: BluetoothBroadcastReceiver,
) : Closeable {

    /**
     * Controls the bluetooth state.
     * STATE SHOULD NEVER BE CHANGED WITHOUT USER CONSENT!
     */
    fun toggleBluetooth(state: Boolean) = if (bluetooth != null) { if (state) bluetooth.enable() else bluetooth.disable() } else null

    @Synchronized fun startDiscovery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION), 1)
            }
        }
        cancelDiscovery()
        if(bluetooth != null && !bluetooth.startDiscovery()) {
            Toast.makeText(activity, "Failed to start bluetooth discovery!", Toast.LENGTH_LONG).show()
        }
    }

    @SuppressWarnings
    fun cancelDiscovery() = if (bluetooth != null && bluetooth.isDiscovering) { bluetooth.cancelDiscovery() } else null

    @Synchronized fun pair(device: BluetoothDevice) : Boolean {
        cancelDiscovery()
        return device.createBond()
    }
    fun paired(device: BluetoothDevice) : Boolean = bluetooth?.bondedDevices?.contains(device) ?: false

    override fun close() {
        bluetoothBroadcastReceiver.close()
    }
}