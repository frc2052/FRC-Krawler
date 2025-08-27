package com.team2052.frckrawler.bluetooth.client

import android.bluetooth.BluetoothDevice

internal sealed class DevicePairingResult {
  class DevicePaired(val device: BluetoothDevice) : DevicePairingResult()
  object Cancelled : DevicePairingResult()
}