package com.team2052.frckrawler.bluetooth.client

import android.bluetooth.BluetoothDevice

sealed class ServerConnectionResult {
  class ServerConnected(val server: BluetoothDevice) : ServerConnectionResult()
  object PairingFailed: ServerConnectionResult()
  object NoFrcKrawlerServiceFound: ServerConnectionResult()
  class Cancelled(val message: CharSequence?) : ServerConnectionResult()
}