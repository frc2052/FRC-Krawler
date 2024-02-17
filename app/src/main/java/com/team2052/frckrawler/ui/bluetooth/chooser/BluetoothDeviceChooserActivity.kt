package com.team2052.frckrawler.ui.bluetooth.chooser

import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import com.team2052.frckrawler.R
import com.team2052.frckrawler.ui.FrcKrawlerPreview
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Activity that allows the user to select a Bluetooth device to connect to.
 * Only used when CompanionDeviceManager is unavailable (< API 26)
 */
@AndroidEntryPoint
class BluetoothDeviceChooserActivity : FragmentActivity() {
  companion object {
    const val EXTRA_DEVICE = "com.team2052.frckrawler.DEVICE"
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      FrcKrawlerTheme {
        val viewModel: BluetoothDeviceChooserViewModel = hiltViewModel()

        LaunchedEffect(true) {
          viewModel.beginScanning()
        }

        DeviceChooserDialog(
          devices = viewModel.devices,
          onCancel = { cancelDeviceChooser() },
          onDeviceClicked = { finishWithSelectedDevice(it) }
        )
      }
    }
  }

  private fun cancelDeviceChooser() {
    setResult(Activity.RESULT_CANCELED)
    finish()
  }

  private fun finishWithSelectedDevice(device: BluetoothDevice) {
    val data = Intent().apply {
      putExtra(EXTRA_DEVICE, device)
    }
    setResult(Activity.RESULT_OK, data)
    finish()
  }
}

// TODO make this previewable
@Composable
private fun DeviceChooserDialog(
  devices: List<BluetoothDevice>,
  onCancel: () -> Unit,
  onDeviceClicked: (BluetoothDevice) -> Unit
) {
  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .height(400.dp),
    shape = MaterialTheme.shapes.medium
  ) {
    ConstraintLayout(
      modifier = Modifier.fillMaxSize()
    ) {
      val (title, subtitle, list, buttons) = createRefs()
      Text(
        modifier = Modifier
          .paddingFromBaseline(top = 40.dp)
          .padding(horizontal = 24.dp)
          .constrainAs(title) {
            top.linkTo(parent.top)
          },
        style = MaterialTheme.typography.titleLarge,
        text = stringResource(R.string.bluetooth_scan_choose_dialog_title)
      )
      Text(
        modifier = Modifier
          .padding(horizontal = 24.dp)
          .constrainAs(subtitle) {
            top.linkTo(title.bottom, margin = 8.dp)
          },
        text = stringResource(R.string.bluetooth_scan_choose_dialog_body)
      )
      DeviceList(
        modifier = Modifier.constrainAs(list) {
          height = Dimension.fillToConstraints
          top.linkTo(subtitle.bottom, margin = 16.dp)
          bottom.linkTo(buttons.top, margin = 8.dp)
        },
        devices = devices,
        onDeviceClicked = onDeviceClicked
      )
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .constrainAs(buttons) {
            bottom.linkTo(parent.bottom)
          }
          .padding(8.dp),
        horizontalArrangement = Arrangement.End
      ) {
        TextButton(onClick = onCancel) {
          Text(stringResource(R.string.cancel))
        }
      }
    }
  }
}

@Composable
private fun DeviceList(
  modifier: Modifier = Modifier,
  devices: List<BluetoothDevice>,
  onDeviceClicked: (BluetoothDevice) -> Unit
) {
  Column(
    modifier.verticalScroll(rememberScrollState())
  ) {
    devices.forEach { device ->
      Row(
        modifier = Modifier
          .clickable(
            onClick = { onDeviceClicked(device) }
          )
          .fillMaxWidth()
          .padding(16.dp),
      ) {
        Icon(imageVector = Icons.Default.Bluetooth, contentDescription = null)
        Text(
          style = MaterialTheme.typography.bodyLarge,
          text = device.name ?: device.address
        )
      }
    }

    Box(
      modifier = Modifier.fillMaxWidth(),
      contentAlignment = Alignment.Center
    ) {
      CircularProgressIndicator(
        modifier = Modifier.size(32.dp)
      )
    }
  }
}

@FrcKrawlerPreview
@Composable
private fun DeviceListPreview() {
  FrcKrawlerTheme {
    DeviceChooserDialog(
      devices = emptyList(),
      onCancel = { },
      onDeviceClicked = { }
    )
  }
}