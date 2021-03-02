package com.team2052.frckrawler.bluetooth.configuration

abstract class BluetoothConfiguration
{
    val UUID: String = "d6035ed0-8f10-11e2-9e96-0800200c9a66"
    val SERVICE_NAME: String = "FRCKrawler"

    abstract fun initialize()

    abstract fun close()
}
