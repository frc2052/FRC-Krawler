package com.team2052.frckrawler.bluetooth.client

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.IntentFilter
import android.os.ParcelUuid
import androidx.activity.ComponentActivity
import com.team2052.frckrawler.bluetooth.BluetoothSyncConstants
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.util.Optional
import java.util.concurrent.Executors
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

@SuppressLint("MissingPermission")
class ServerConnectionManager internal @Inject constructor(
  bluetoothAdapterOptional: Optional<BluetoothAdapter>,
  private val discoveryStrategy: ServerDiscoveryStrategy,
  @ApplicationContext private val context: Context
) {

  private val dispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
  private val coroutineContext = dispatcher + SupervisorJob()
  private val bluetoothAdapter = bluetoothAdapterOptional.get()

  /**
   * Get a list of all paired bluetooth devices that are running FRCKrawler servers
   */
  fun getPairedFrcKrawlerServers(): List<BluetoothDevice> {
    val pairedServers = bluetoothAdapter.bondedDevices.filter {
      it.uuids?.contains(ParcelUuid(BluetoothSyncConstants.Uuid)) == true
    }

    return pairedServers
  }

  /**
   * Connect to a new FRCKrawler server.
   *
   * Begins by performing Bluetooth device discovery.
   * Once the user has selected a device, we then attempt to pair with that device.
   * If pairing succeeds, we do a final check that an FRCKrawler server is running on that device.
   */
  suspend fun connectToNewServer(
    activity: ComponentActivity
  ): ServerConnectionResult = withContext(coroutineContext) {
    // Try to find a previously paired device that is still running first.
    var deviceToPair: BluetoothDevice? = getLiveBondedDevice()

    if (deviceToPair == null) {
      val discoveryResult = discoveryStrategy.launchDeviceDiscovery(activity)
      if (discoveryResult is DeviceSelectionResult.Cancelled) {
        return@withContext ServerConnectionResult.Cancelled(discoveryResult.message)
      }

      deviceToPair = (discoveryResult as DeviceSelectionResult.DeviceSelected).device
    }

    val pairedDevice: BluetoothDevice
    if (deviceToPair.bondState != BluetoothDevice.BOND_BONDED) {
      val pairingResult = pairDevice(deviceToPair)
      if (pairingResult !is DevicePairingResult.DeviceParied) {
        return@withContext ServerConnectionResult.PairingFailed
      }
      pairedDevice = pairingResult.device
    } else {
      pairedDevice = deviceToPair
    }

    return@withContext ServerConnectionResult.ServerConnected(pairedDevice)
  }

  fun getLiveBondedDevice(): BluetoothDevice? {
    val pairedServers = getPairedFrcKrawlerServers()
    return pairedServers.firstOrNull { device ->
      try {
        // Just try to create a connection, if it works then the device is broadcasting
        val connection = device.createRfcommSocketToServiceRecord(BluetoothSyncConstants.Uuid)
        connection.connect()
        connection.close()
        true
      } catch (_: Exception) {
        false
      }
    }
  }

  private suspend fun pairDevice(
    device: BluetoothDevice
  ): DevicePairingResult = suspendCoroutine { continuation ->
    var receiver = ServerPairingBroadcastReceiver(
      deviceToPair = device,
      onBonded = { continuation.resume(DevicePairingResult.DeviceParied(device)) },
      onCanceled = { continuation.resume(DevicePairingResult.Cancelled) }
    )

    context.registerReceiver(receiver, IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED))
    device.createBond()
  }

}