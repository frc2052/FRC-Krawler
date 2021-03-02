package com.team2052.frckrawler.bluetooth

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import com.team2052.frckrawler.bluetooth.configuration.BluetoothConfiguration

class BluetoothManager(private val context: Context)
{
    private val bluetoothAdapter : BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    private var bluetoothCapabilities: BluetoothCapabilities? = null
    private var bluetoothConfiguration: BluetoothConfiguration? = null

    fun setupBluetoothCapabilities(activity: Activity)
    {
        bluetoothCapabilities = BluetoothCapabilities.createBluetoothCapabilities(context, bluetoothAdapter)

        bluetoothCapabilities?.bluetoothStateChangeListener = BluetoothStateChangeListener()
        {
            if(it == BluetoothCapabilities.BluetoothState.BLUETOOTH_DISABLED)
            {
                requestBluetoothEnable(activity)
            }
        }
        bluetoothCapabilities?.registerStateChangeListener()

        if(bluetoothCapabilities?.bluetoothState == BluetoothCapabilities.BluetoothState.BLUETOOTH_DISABLED)
        {
            requestBluetoothEnable(activity)
        }
    }

    fun setBluetoothConfiguration(bluetoothConfiguration: BluetoothConfiguration): BluetoothConfiguration?
    {
        this.bluetoothConfiguration = bluetoothConfiguration
        this.bluetoothConfiguration?.initialize()
        return this.bluetoothConfiguration
    }

    fun getBluetoothCapabilities(): BluetoothCapabilities?
    {
        return bluetoothCapabilities
    }

    fun requestBluetoothEnable(activity: Activity)
    {
        val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        activity.startActivityForResult(enableBluetoothIntent, RequestCodes.BLUETOOTH_ENABLE_REQUEST_CODE)
    }

    fun cleanup()
    {
        bluetoothCapabilities?.unregisterStateChangeListener()
        bluetoothConfiguration?.close()
    }

    companion object RequestCodes
    {
        const val BLUETOOTH_ENABLE_REQUEST_CODE: Int = 2052
    }
}