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
import com.team2052.frckrawler.util.BluetoothUtil
import com.team2052.frckrawler.util.BluetoothUtil.requireBluetooth
import java.io.Closeable
import javax.inject.Inject
import kotlin.reflect.KProperty

// TODO: Implement hilt
class BluetoothController @Inject constructor(
    private val context: Context,
    private val bluetooth: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter(),
) : Closeable {

    init {
        BluetoothUtil.bluetooth = bluetooth
    }

    /**
     * BLUETOOTH STATE SHOULD NEVER BE CHANGED WITHOUT USER CONSENT!
     */
    fun toggleBluetooth(state: Boolean) {
        requireBluetooth { bluetooth ->
            if (state) bluetooth.enable() else bluetooth.disable()
        }
    }

    fun bluetoothState(): Boolean =
        requireBluetooth { bluetooth ->
            return bluetooth.isEnabled
        } ?: false

    fun bondedDevices(): Set<BluetoothDevice> =
        requireBluetooth { bluetooth ->
            bluetooth.bondedDevices
        } ?: emptySet()

    fun makeDiscoverable(duration: Int) {
        requireBluetooth {
            val discoverableIntent: Intent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
                putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, duration)
                // TODO: find better way to do this
            }.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(discoverableIntent)
        }
    }

    fun startDiscovery(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                activity.requestPermissions(arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION), 0)
            }
        }
        if (cancelDiscovery()) {
            bluetooth!!.startDiscovery()
        }
    }

    fun cancelDiscovery(): Boolean =
        requireBluetooth { bluetooth ->
            bluetooth.cancelDiscovery()
        } ?: false

    // OLD

    @Synchronized fun pair(device: BluetoothDevice) : Boolean {
        cancelDiscovery()
        return device.createBond()
    }

    fun paired(device: BluetoothDevice) : Boolean =
        bluetooth?.bondedDevices?.contains(device) ?: false

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

    }
}