package com.team2052.frckrawler.ui.bluetooth

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.LocalAbsoluteElevation
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.team2052.frckrawler.R
import com.team2052.frckrawler.data.model.DeviceType
import com.team2052.frckrawler.ui.components.Alert

/**
 * Launch an activity to enable bluetooth.
 * If the user does not enable bluetooth, show a dialog explaining that we need bluetooth
 */
@Composable
fun RequestEnableBluetooth(
  deviceType: DeviceType,
  onEnabled: () -> Unit,
  onCanceled: () -> Unit
) {
  var showCancelDialog by remember { mutableStateOf(false) }
  val launcher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.StartActivityForResult(),
    onResult = {
      if (it.resultCode == Activity.RESULT_OK) {
        onEnabled()
      } else {
        showCancelDialog = true
      }
    }
  )

  LaunchedEffect(true) {
    launcher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
  }

  if (showCancelDialog) {
    CompositionLocalProvider(LocalAbsoluteElevation provides 2.dp) {
      Alert(
        title = { Text(stringResource(R.string.bluetooth_enable_title)) },
        description = {
          val descriptionRes = when (deviceType) {
            DeviceType.SERVER -> R.string.bluetooth_enable_server_description
            DeviceType.CLIENT -> R.string.bluetooth_enable_client_description
          }
          Text(stringResource(descriptionRes))
        },
        confirm = { Text(stringResource(R.string.ok)) },
        onStateChange = {
          showCancelDialog = false
          onCanceled()
        }
      )
    }
  }
}