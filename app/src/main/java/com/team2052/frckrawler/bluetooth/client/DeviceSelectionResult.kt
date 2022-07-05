package com.team2052.frckrawler.bluetooth.client

import android.bluetooth.BluetoothDevice

sealed class DeviceSelectionResult {
  class DeviceSelected(val device: BluetoothDevice) : DeviceSelectionResult()
  class Cancelled(val message: CharSequence? = null) : DeviceSelectionResult()
}