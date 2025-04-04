package com.team2052.frckrawler.ui.permissions

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Real permission manager implementation that checks a [Context] for permission status
 */
class RealPermissionManager @Inject constructor(
  @ApplicationContext private val context: Context
) : PermissionManager {
  override fun hasPermission(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
  }

  override fun hasPermissions(permissions: List<String>): Boolean {
    return permissions.all {
      ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }
  }
}