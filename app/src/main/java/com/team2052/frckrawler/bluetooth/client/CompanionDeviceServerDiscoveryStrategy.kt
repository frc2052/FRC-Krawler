package com.team2052.frckrawler.bluetooth.client

import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.companion.AssociationRequest
import android.companion.BluetoothDeviceFilter
import android.companion.CompanionDeviceManager
import android.content.Context
import android.content.IntentSender
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.IntentCompat
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@RequiresApi(Build.VERSION_CODES.O)
internal class CompanionDeviceServerDiscoveryStrategy @Inject constructor() :
  ServerDiscoveryStrategy {

  override suspend fun launchDeviceDiscovery(
    activity: ComponentActivity
  ): DeviceSelectionResult = suspendCoroutine { continuation ->
    val deviceManager: CompanionDeviceManager =
      activity.getSystemService(Context.COMPANION_DEVICE_SERVICE) as CompanionDeviceManager

    val deviceFilter = BluetoothDeviceFilter.Builder()
      .build()

    val pairingRequest = AssociationRequest.Builder()
      .addDeviceFilter(deviceFilter)
      .build()

    deviceManager.associate(
      pairingRequest,
      object : CompanionDeviceManager.Callback() {
        var failedResumed: Boolean = false
        
        // Called when a device is found. Launch the IntentSender so the user
        // can select the device they want to pair with.
        @Deprecated("Deprecated in Java")
        override fun onDeviceFound(chooserLauncher: IntentSender) {
          val launcher = activity.activityResultRegistry.register(
            ServerDiscoveryStrategy.LAUNCHER_KEY,
            ActivityResultContracts.StartIntentSenderForResult()
          ) {
            if (!failedResumed) {
              continuation.resume(getSelectionResult(it))
            }
          }
          launcher.launch(IntentSenderRequest.Builder(chooserLauncher).build())
        }

        override fun onFailure(error: CharSequence?) {
          continuation.resume(DeviceSelectionResult.Cancelled(error))
          failedResumed = true
        }
      }, null
    )
  }

  private fun getSelectionResult(
    result: ActivityResult
  ): DeviceSelectionResult {
    when (result.resultCode) {
      Activity.RESULT_OK -> {
        val deviceToPair: BluetoothDevice? = result.data?.let { data ->
          IntentCompat.getParcelableExtra(data, CompanionDeviceManager.EXTRA_DEVICE, BluetoothDevice::class.java)
        }
        if (deviceToPair != null) {
          return DeviceSelectionResult.DeviceSelected(deviceToPair)
        }
      }
    }

    return DeviceSelectionResult.Cancelled()
  }
}

