package com.team2052.frckrawler.bluetooth

fun interface BluetoothStateChangeListener
{
    fun onStateChange(bluetoothState: BluetoothCapabilities.BluetoothState)
}