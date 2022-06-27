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
    val bluetoothAdapter: BluetoothAdapter,
) : Closeable {

    fun enableBluetooth() {
        if (!bluetoothAdapter.isEnabled) bluetoothAdapter.enable()
    }

    fun disableBluetooth() {
        if (!bluetoothAdapter.isEnabled) bluetoothAdapter.disable()
    }

    fun toggleBluetooth(state: Boolean) {
        if (bluetoothAdapter.isEnabled != state) {
            if (state) bluetoothAdapter.enable() else bluetoothAdapter.disable()
        }
    }

    fun bluetoothEnabled(): Boolean {
        return bluetoothAdapter.isEnabled
    }

    fun bondedDevices(): Set<BluetoothDevice> {
        return bluetoothAdapter.bondedDevices
    }

    fun makeDiscoverable(duration: Int) {
        val discoverableIntent: Intent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
            putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, duration)
        }.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // TODO: find better way to do this
        context.startActivity(discoverableIntent)

    }

    fun startDiscovery(onDeviceFound: (BluetoothDevice) -> Unit) {
        if (cancelDiscovery()) bluetoothAdapter.startDiscovery()
    }

    fun cancelDiscovery(): Boolean = bluetoothAdapter.cancelDiscovery()


    fun bond(device: BluetoothDevice) : Boolean {
        cancelDiscovery()
        return device.createBond()
    }

    fun bonded(device: BluetoothDevice) : Boolean =
        bluetoothAdapter.bondedDevices?.contains(device) ?: false

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