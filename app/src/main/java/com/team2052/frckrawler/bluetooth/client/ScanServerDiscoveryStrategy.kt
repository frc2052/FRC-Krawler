package com.team2052.frckrawler.bluetooth.client

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.team2052.frckrawler.ui.bluetooth.chooser.BluetoothDeviceChooserActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

internal class ScanServerDiscoveryStrategy @Inject constructor(
  private val bluetoothAdapter: BluetoothAdapter,
  @ApplicationContext private val context: Context
) : ServerDiscoveryStrategy {
  companion object {
    private const val LAUNCHER_KEY = "com.team2052.com.frckrawler.bluetooth.client.DEVICE_CHOOSER"
  }

  override suspend fun launchDeviceDiscovery(
    activity: ComponentActivity
  ): DeviceSelectionResult = suspendCoroutine { continuation ->
    val launcher = activity.activityResultRegistry.register(
      LAUNCHER_KEY,
      ActivityResultContracts.StartActivityForResult()
    ) {
      continuation.resume(getSelectionResult(it))
    }

    launcher.launch(
      Intent(context, BluetoothDeviceChooserActivity::class.java)
    )
  }

  private fun getSelectionResult(
    result: ActivityResult
  ): DeviceSelectionResult {
    when (result.resultCode) {
      Activity.RESULT_OK -> {
        val deviceToPair: BluetoothDevice? =
          result.data?.getParcelableExtra(BluetoothDeviceChooserActivity.EXTRA_DEVICE)
        if (deviceToPair != null) {
          return DeviceSelectionResult.DeviceSelected(deviceToPair)
        }
      }
    }

    return DeviceSelectionResult.Cancelled()
  }
}