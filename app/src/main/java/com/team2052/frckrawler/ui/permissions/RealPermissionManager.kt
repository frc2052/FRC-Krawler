package com.team2052.frckrawler.ui.permissions

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.team2052.frckrawler.di.ApplicationContext
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

/**
 * Real permission manager implementation that checks a [Context] for permission status
 */

@ContributesBinding(AppScope::class)
@Inject
class RealPermissionManager(
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