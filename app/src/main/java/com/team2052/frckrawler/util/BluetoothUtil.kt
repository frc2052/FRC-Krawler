package com.team2052.frckrawler.util

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice

object BluetoothUtil {

    var bluetooth: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    inline fun <T> requireBluetooth(block: (BluetoothAdapter) -> T): T? =
        if (bluetooth != null) { block(bluetooth!!) } else { null }

    fun getDeviceNames(devices: List<BluetoothDevice>?): List<String?> {
        if(devices != null) {
            return List<String?>(devices.size) { device -> devices[device].name }
        }
        return emptyList()
    }
}