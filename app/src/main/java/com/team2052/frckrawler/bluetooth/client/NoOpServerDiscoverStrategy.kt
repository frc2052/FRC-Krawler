package com.team2052.frckrawler.bluetooth.client

import androidx.activity.ComponentActivity

/**
 * This discovery strategy does nothing, and is intended as a
 * default behavior when bluetooth is not available.
 */
internal object NoOpServerDiscoverStrategy :
  ServerDiscoveryStrategy {

  override suspend fun launchDeviceDiscovery(
    activity: ComponentActivity
  ): DeviceSelectionResult = DeviceSelectionResult.Cancelled()
}

