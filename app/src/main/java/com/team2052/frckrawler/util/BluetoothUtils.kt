package com.team2052.frckrawler.util

import android.bluetooth.BluetoothDevice

object BluetoothUtils {
    fun getDeviceNames(devices: List<BluetoothDevice>?): List<String?> {
        if(devices != null) {
            return List<String?>(devices.size) { device -> devices[device].name }
        }
        return emptyList()
    }
}