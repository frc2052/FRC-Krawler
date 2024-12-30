package com.team2052.frckrawler.bluetooth.client

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.IntentCompat

/**
 * Listens for ACTION_BOND_STATE_CHANGED and calls [onBonded] once the desired device is paired.
 */
class ServerPairingBroadcastReceiver(
  private val deviceToPair: BluetoothDevice,
  private val onBonded: () -> Unit,
  private val onCanceled: () -> Unit
) : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {
    when (intent.action) {
      BluetoothDevice.ACTION_BOND_STATE_CHANGED -> {
        val device: BluetoothDevice? = IntentCompat.getParcelableExtra(intent, BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
        val bondState: Int = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, 0)
        val previousState: Int = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, 0)

        if (deviceToPair.address == device?.address) {
          when (bondState) {
            BluetoothDevice.BOND_BONDED -> {
              onBonded()
              context.unregisterReceiver(this)
            }

            BluetoothDevice.BOND_NONE -> {
              if (previousState == BluetoothDevice.BOND_BONDING) {
                // We were bonding and are not anymore, so it was canceled
                onCanceled()
                context.unregisterReceiver(this)
              }
            }
          }
        }
      }
    }
  }
}