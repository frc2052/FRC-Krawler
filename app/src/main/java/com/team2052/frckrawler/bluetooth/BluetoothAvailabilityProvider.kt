package com.team2052.frckrawler.bluetooth

import android.content.Context
import android.content.pm.PackageManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class BluetoothAvailabilityProvider @Inject constructor(
  @ApplicationContext val context: Context
) {
  val availability: BluetoothAvailability = BluetoothAvailability(
    isAvailable = context.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)
  )
}