package com.team2052.frckrawler.ui.permissions

import android.os.Build

/**
 * Permissions required to use the app, split into client and server.
 * These should be request when starting the server or first connecting a client to a server.
 */
object RequiredPermissions {
  val serverPermissions = if (Build.VERSION.SDK_INT >= 31) {
    listOf(
      android.Manifest.permission.BLUETOOTH_CONNECT,
    )
  } else if (Build.VERSION.SDK_INT >= 26) {
    // We will use CompanionDeviceManager on API 26+
    emptyList()
  } else {
    listOf(
      android.Manifest.permission.ACCESS_COARSE_LOCATION
    )
  }

  val clientPermissions = if (Build.VERSION.SDK_INT >= 31) {
    listOf(
      android.Manifest.permission.BLUETOOTH_SCAN,
      android.Manifest.permission.BLUETOOTH_CONNECT,
    )
  } else if (Build.VERSION.SDK_INT >= 26) {
    // We will use CompanionDeviceManager on API 26+
    emptyList()
  } else {
    listOf(
      android.Manifest.permission.ACCESS_COARSE_LOCATION
    )
  }
}