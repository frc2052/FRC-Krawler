package com.team2052.frckrawler.bluetooth

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import com.team2052.frckrawler.bluetooth.configuration.BluetoothConfiguration

private val TAG = BluetoothManager::class.simpleName

class BluetoothManager(private val context: Context) {

    private val bluetoothAdapter : BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    private lateinit var bluetoothCapabilities: BluetoothCapabilities
    private lateinit var bluetoothConfiguration: BluetoothConfiguration
    private lateinit var bluetoothDiscoverer: BluetoothDiscoverer

    fun setupBluetoothCapabilities(activity: Activity) {
        bluetoothCapabilities = BluetoothCapabilities.createBluetoothCapabilities(context, bluetoothAdapter)

        bluetoothCapabilities.bluetoothStateChangeListener = BluetoothStateChangeListener { bluetoothState ->
            if(bluetoothState == BluetoothCapabilities.BluetoothState.BLUETOOTH_DISABLED) {
                requestBluetoothEnable(activity)
            }
        }
        bluetoothCapabilities.registerStateChangeListener()

        if(bluetoothCapabilities.bluetoothState == BluetoothCapabilities.BluetoothState.BLUETOOTH_DISABLED) {
            requestBluetoothEnable(activity)
        }
    }

    fun getBluetoothCapabilities(): BluetoothCapabilities = bluetoothCapabilities

    fun <T : BluetoothConfiguration>setBluetoothConfiguration(bluetoothConfiguration: T) {
        this.bluetoothConfiguration = bluetoothConfiguration.initialize<T>(bluetoothAdapter!!)
    }

    // returns bluetoothConfiguration casted to specific bluetooth configuration
    fun <T : BluetoothConfiguration>getBluetoothConfiguration(): T = bluetoothConfiguration as T

    // checks if generic bluetoothConfiguration is of type passed bluetoothConfiguration
    inline fun <reified T: BluetoothConfiguration>checkBluetoothConfigurationType(bluetoothConfiguration: BluetoothConfiguration): Boolean {
        val primaryConstructor = T::class.constructors.find { it.parameters.isEmpty() }
        val clazz: T? = primaryConstructor?.call()
        return bluetoothConfiguration.javaClass.isAssignableFrom(clazz?.javaClass)
    }

    fun setupBluetoothDiscoverer() {
        bluetoothDiscoverer = BluetoothDiscoverer(bluetoothAdapter, 0)
    }

    fun getBluetoothDiscoverer(): BluetoothDiscoverer = bluetoothDiscoverer

    fun requestBluetoothEnable(activity: Activity) {
        val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        activity.startActivityForResult(enableBluetoothIntent, BluetoothConstants.BLUETOOTH_ENABLE_REQUEST_CODE)
    }

    fun cleanup() {
        bluetoothCapabilities.unregisterStateChangeListener()
        bluetoothConfiguration.close(context)
        bluetoothDiscoverer.endDiscovery()
    }

    object BluetoothConstants {
        const val BLUETOOTH_ENABLE_REQUEST_CODE: Int = 2052
        const val UUID: String = "d6035ed0-8f10-11e2-9e96-0800200c9a66"
        const val SERVICE_NAME: String = "FRCKrawler"
        const val OK = 1
        const val VERSION_ERROR = -1
        const val EVENT_MATCH_ERROR = -2
        const val SCOUT_SYNC = 1
    }
}