package com.team2052.frckrawler.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.Closeable
import javax.inject.Inject

class BluetoothController @Inject constructor(
    @ApplicationContext private val context: Context,
    val bluetooth: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter(),
) : Closeable {

    fun toggleBluetooth(state: Boolean) {
        requireBluetooth { bluetooth ->
            if (bluetooth.isEnabled != state) {
                if (state) bluetooth.enable() else bluetooth.disable()
            }
        }
    }

    fun bluetoothState(): Boolean =
        requireBluetooth { bluetooth ->
            bluetooth.isEnabled
        } ?: false

    fun bondedDevices(): Set<BluetoothDevice> =
        requireBluetooth { bluetooth ->
            bluetooth.bondedDevices
        } ?: emptySet()

    fun makeDiscoverable(duration: Int) {
        requireBluetooth {
            val discoverableIntent: Intent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
                putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, duration)
            }.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // TODO: find better way to do this
            context.startActivity(discoverableIntent)
        }
    }

    fun startDiscovery(onDeviceFound: (BluetoothDevice) -> Unit) {
        requireBluetooth { bluetooth ->
            if (cancelDiscovery()) bluetooth.startDiscovery()
        }
    }

    fun cancelDiscovery(): Boolean =
        requireBluetooth { bluetooth ->
            bluetooth.cancelDiscovery()
        } ?: false

    fun bond(device: BluetoothDevice) : Boolean {
        cancelDiscovery()
        return device.createBond()
    }

    fun bonded(device: BluetoothDevice) : Boolean =
        bluetooth?.bondedDevices?.contains(device) ?: false

    private inline fun <T> requireBluetooth(block: (BluetoothAdapter) -> T): T? =
        if (bluetooth != null && bluetoothAvailable) { block(bluetooth) } else { null }

    override fun close() {
        cancelDiscovery()
    }

    companion object {
        private var bluetoothAvailable = true

        fun bluetoothAvailable(bluetoothAvailable: Boolean = true) {
            this.bluetoothAvailable = bluetoothAvailable
        }
    }
}