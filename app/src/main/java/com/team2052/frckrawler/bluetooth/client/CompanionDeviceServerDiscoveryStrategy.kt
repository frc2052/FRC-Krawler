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

    // Theoretically we could filter for our service ID, but that causes an NPE
    // on older android versions: https://issuetracker.google.com/issues/124106032
    // No device filter also allows us to pair without the server running
    val pairingRequest = AssociationRequest.Builder()
      .build()

    deviceManager.associate(
      pairingRequest,
      object : CompanionDeviceManager.Callback() {
        var resumed: Boolean = false
        
        // Called when a device is found. Launch the IntentSender so the user
        // can select the device they want to pair with.
        @Deprecated("Deprecated in Java")
        override fun onDeviceFound(chooserLauncher: IntentSender) {
          val launcher = activity.activityResultRegistry.register(
            ServerDiscoveryStrategy.LAUNCHER_KEY,
            ActivityResultContracts.StartIntentSenderForResult()
          ) {
            if (!resumed) {
              continuation.resume(getSelectionResult(it))
              resumed = true
            }
          }
          launcher.launch(IntentSenderRequest.Builder(chooserLauncher).build())
        }

        override fun onFailure(error: CharSequence?) {
          if (!resumed) {
            continuation.resume(DeviceSelectionResult.Cancelled(error))
            resumed = true
          }
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

