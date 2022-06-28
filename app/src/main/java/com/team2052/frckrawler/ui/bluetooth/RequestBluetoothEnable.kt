package com.team2052.frckrawler.ui

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
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
    Alert(
      title = { Text(stringResource(R.string.bluetooth_enable_title)) },
      description = {
        val descriptionRes = when (deviceType) {
          DeviceType.Server -> R.string.bluetooth_enable_server_description
          DeviceType.Client -> R.string.bluetooth_enable_client_description
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