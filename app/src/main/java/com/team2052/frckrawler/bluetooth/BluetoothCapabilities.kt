package com.team2052.frckrawler.bluetooth

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log

private val TAG = BluetoothCapabilities::class.simpleName

class BluetoothCapabilities(
    private val context: Context,
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter(),
    var bluetoothState: BluetoothState = BluetoothState.BLUETOOTH_DISABLED
) : BroadcastReceiver() {

    var bluetoothStateChangeListener: BluetoothStateChangeListener? = null
    private val bluetoothStateChangeIntentFilter = IntentFilter().apply {
        this.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
    }

    fun checkBluetoothConnection(): BluetoothState {
        if(bluetoothSupportCheck()) {
            return BluetoothState.BLUETOOTH_NOT_SUPPORTED
        }

        bluetoothState = if(bluetoothAdapter != null && bluetoothAdapter.isEnabled) {
            BluetoothState.BLUETOOTH_ENABLED
        } else {
            BluetoothState.BLUETOOTH_DISABLED
        }
        return bluetoothState
    }

    private fun bluetoothSupportCheck(): Boolean {
        return bluetoothState == BluetoothState.BLUETOOTH_NOT_SUPPORTED
    }

    override fun onReceive(
        context: Context, intent: Intent) {
        if(intent.action == BluetoothAdapter.ACTION_STATE_CHANGED && bluetoothStateChangeListener != null) {
            when(intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)) {
                BluetoothAdapter.STATE_ON -> {
                    bluetoothState = BluetoothState.BLUETOOTH_ENABLED
                    bluetoothStateChangeListener?.onStateChange(bluetoothState)
                }
                BluetoothAdapter.STATE_OFF -> {
                    bluetoothState = BluetoothState.BLUETOOTH_DISABLED
                    bluetoothStateChangeListener?.onStateChange(bluetoothState)
                }
            }
        }
    }

    fun registerStateChangeListener() {
        context.registerReceiver(this, bluetoothStateChangeIntentFilter)
        if(bluetoothStateChangeListener == null) {
            Log.w(TAG, "Registering state change listener with null listener, any received events will be ignored")
        }
    }

    fun unregisterStateChangeListener() {
        context.unregisterReceiver(this)
    }

    companion object {
        fun createBluetoothCapabilities(context: Context, bluetoothAdapter: BluetoothAdapter?): BluetoothCapabilities {
            return if(bluetoothAdapter != null) {
                BluetoothCapabilities(context, bluetoothAdapter, BluetoothState.BLUETOOTH_DISABLED)
            } else {
                BluetoothCapabilities(context, bluetoothAdapter, BluetoothState.BLUETOOTH_NOT_SUPPORTED)
            }
        }
    }

    enum class BluetoothState {
        BLUETOOTH_ENABLED,
        BLUETOOTH_DISABLED,
        BLUETOOTH_NOT_SUPPORTED
    }
}