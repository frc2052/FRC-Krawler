package com.team2052.frckrawler.bluetooth.client

import androidx.activity.ComponentActivity

/**
 * Strategy for discovering an FRC Krawler server.
 *
 * On API 26+ we use [CompanionDeviceServerDiscoveryStrategy], which uses Android's
 * CompanionDeviceManager.
 * Below API 26 we fallback to regular ol' Bluetooth scanning.
 */
interface ServerDiscoveryStrategy {

  companion object {
    const val LAUNCHER_KEY = "com.team2052.com.frckrawler.companiondevice"
  }

  /**
   * Create an activity launcher that will allow the user to choose a Bluetooth device to pair with
   */
  suspend fun launchDeviceDiscovery(
    activity: ComponentActivity
  ): DeviceSelectionResult

}