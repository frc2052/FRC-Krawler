package com.team2052.frckrawler.bluetooth.client

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.ParcelUuid
import android.os.Parcelable

/**
 * Listens for ACTION_UUID and calls [onServicesDiscovered] with the discovered UUIDs
 */
class SdpDiscoveryBroadcastReceiver(
  private val onServicesDiscovered: (List<ParcelUuid>) -> Unit
) : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {
    when (intent.action) {
      BluetoothDevice.ACTION_UUID -> {
        val parcels: Array<Parcelable>? = intent.getParcelableArrayExtra(BluetoothDevice.EXTRA_UUID)
        val uuids = parcels?.map { it as ParcelUuid } ?: emptyList()
        onServicesDiscovered(uuids)
        context.unregisterReceiver(this)
      }
    }
  }
}