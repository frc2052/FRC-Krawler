package com.team2052.frckrawler.ui.permissions

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.team2052.frckrawler.R
import com.team2052.frckrawler.data.model.DeviceType
import com.team2052.frckrawler.ui.components.Alert
import com.team2052.frckrawler.ui.components.AlertState
import com.team2052.frckrawler.ui.components.ComposableLauncher
import com.team2052.frckrawler.ui.permissions.RequiredPermissions.serverPermissions
import kotlinx.coroutines.launch


/**
 * Shows permission request dialogs if needed
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun BluetoothPermissionRequestDialogs(
  deviceType: DeviceType,
  onAllPermissionsGranted: () -> Unit,
  onCanceled: () -> Unit
) {
  val permissions = rememberMultiplePermissionsState(
    permissions = when (deviceType) {
      DeviceType.Client -> RequiredPermissions.clientPermissions
      DeviceType.Server -> RequiredPermissions.serverPermissions
    }
  )

  if (!permissions.permissionRequested) {
    if (permissions.shouldShowRationale) {
      Alert(
        confirm = { Text(stringResource(R.string.ok)) },
        title = { Text(stringResource(R.string.bluetooth_permission_rationale_title)) },
        description = {
          val descriptionRes = when (deviceType) {
            DeviceType.Client -> R.string.bluetooth_permission_rationale_description_client
            DeviceType.Server -> R.string.bluetooth_permission_rationale_description_server
          }
          Text(stringResource(descriptionRes))
        },
        onStateChange = { permissions.launchMultiplePermissionRequest() }
      )
    } else {
      LaunchedEffect(true) {
        permissions.launchMultiplePermissionRequest()
      }
    }
  }

  if (permissions.permissionRequested && permissions.revokedPermissions.isNotEmpty()) {
    val settingsIntent = getSettingsIntent()
    val context = LocalContext.current

    Alert(
      confirm = { Text(stringResource(R.string.ok)) },
      dismiss = { Text(stringResource(R.string.cancel)) },
      title = { Text(stringResource(R.string.bluetooth_denied_title)) },
      description = {
        val descriptionRes = when (deviceType) {
          DeviceType.Client -> R.string.bluetooth_denied_description_server
          DeviceType.Server -> R.string.bluetooth_denied_description_client
        }
        Text(stringResource(descriptionRes))
      },
      onStateChange = { state ->
        if (state == AlertState.CONFIRMED) {
          context.startActivity(settingsIntent)
        }

        // We don't know if they did it or not, they need to re-try manually.
        onCanceled()
      }
    )
  }

  if (permissions.allPermissionsGranted) {
    LaunchedEffect(true) {
      onAllPermissionsGranted()
    }
  }
}

@Composable
private fun getSettingsIntent(): Intent {
  val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
  intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
  val uri = Uri.fromParts("package", LocalContext.current.packageName, null)
  intent.data = uri

  return intent
}

