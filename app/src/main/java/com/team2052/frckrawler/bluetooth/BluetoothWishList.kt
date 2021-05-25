package com.team2052.frckrawler.bluetooth

import android.bluetooth.BluetoothDevice
import java.io.Closeable

/**
 * Works with non bluetooth compatible devices (never crashes!)
 */
interface BluetoothWishList {

    fun toggleBluetooth()

    //fun getPairedDevices

    class BluetoothWishList {

        fun toggleBluetooth() {

        }

        /* vvv HIGHER LEVEL STUFF vvv */

        fun startDiscovery() {

        }

        fun cancelDiscovery() {

        }

        fun pair(device: BluetoothDevice) {

        }

        fun paired(device: BluetoothDevice) {

        }
    }

    class BluetoothBroadcastReceiver(
        /* INCLUDE EVENT HANDLING FUNCTIONS */
    ) : Closeable {

        override fun close() {

        }
    }
}