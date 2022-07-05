package com.team2052.frckrawler.bluetooth.client

import android.bluetooth.BluetoothDevice

internal sealed class DevicePairingResult {
  class DeviceParied(val device: BluetoothDevice) : DevicePairingResult()
  object TimedOut : DevicePairingResult()
  object Cancelled : DevicePairingResult()
}