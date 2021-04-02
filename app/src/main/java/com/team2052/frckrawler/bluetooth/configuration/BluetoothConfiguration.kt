package com.team2052.frckrawler.bluetooth.configuration

import android.bluetooth.BluetoothAdapter
import android.content.Context

abstract class BluetoothConfiguration() {

    protected var bluetoothAdapter: BluetoothAdapter? = null

    protected var initialized: Boolean = false
    open fun <T : BluetoothConfiguration>initialize(bluetoothAdapter: BluetoothAdapter): T {
        this.bluetoothAdapter = bluetoothAdapter
        initialized = true
        return this as T
    }

    abstract fun run(context: Context)

    abstract fun close(context: Context)
}
