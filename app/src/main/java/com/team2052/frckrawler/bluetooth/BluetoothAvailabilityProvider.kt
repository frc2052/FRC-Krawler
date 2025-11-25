package com.team2052.frckrawler.bluetooth

import android.content.Context
import android.content.pm.PackageManager
import com.team2052.frckrawler.di.ApplicationContext
import dev.zacsweers.metro.Inject

@Inject
class BluetoothAvailabilityProvider(
  @ApplicationContext val context: Context
) {
  val availability: BluetoothAvailability = BluetoothAvailability(
    isAvailable = context.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)
  )
}