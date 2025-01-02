package com.team2052.frckrawler.ui.bluetooth.chooser

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.compose.runtime.mutableStateListOf
import androidx.core.content.IntentCompat
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Optional
import javax.inject.Inject

@HiltViewModel
@SuppressLint("MissingPermission")
class BluetoothDeviceChooserViewModel @Inject constructor(
  bluetoothAdapterOptional: Optional<BluetoothAdapter>,
  @ApplicationContext private val context: Context
) : ViewModel() {
  val bluetoothAdapter = bluetoothAdapterOptional.get()
  val devices = mutableStateListOf<BluetoothDevice>()

  private val receiver = DeviceScanReceiver(
    onFound = {
      devices.add(it)
    }
  )

  fun beginScanning() {
    devices.clear()

    val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
    context.registerReceiver(receiver, filter)
    bluetoothAdapter.startDiscovery()
  }

  override fun onCleared() {
    context.unregisterReceiver(receiver)
    bluetoothAdapter.cancelDiscovery()
  }
}

private class DeviceScanReceiver(
  private val onFound: (BluetoothDevice) -> Unit
) : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {
    // TODO could also listen to ACTION_SCAN_MODE_CHANGED to hide the progress bar when scanning is done
    when (intent.action) {
      BluetoothDevice.ACTION_FOUND -> {
        val device: BluetoothDevice? = IntentCompat.getParcelableExtra(intent, BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
        device?.let {
          onFound(it)
        }
      }
    }
  }
}