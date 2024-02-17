package com.team2052.frckrawler.ui.server.home

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import com.team2052.frckrawler.R
import com.team2052.frckrawler.data.model.RemoteScout
import com.team2052.frckrawler.ui.components.Card
import com.team2052.frckrawler.ui.components.CardHeader
import java.time.format.DateTimeFormatter

@Composable
internal fun ConnectedScoutsList(
  modifier: Modifier = Modifier,
  scouts: List<RemoteScout>
) {
  Card(
    modifier = modifier,
    header = {
      CardHeader(
        title = { Text(stringResource(R.string.server_connected_scouts_title)) }
      )
    },
  ) {
    val context = LocalContext.current
    Button(
      onClick = {
        val discoverableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
          putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 60)
        }
        context.startActivity(discoverableIntent)
      }
    ) {
      Text(stringResource(R.string.server_connect_new_scouts_button))
    }

    Column {
      scouts.forEach { scout ->
        SyncedScout(scout)
      }
    }
  }
}

@Composable
private fun SyncedScout(
  scout: RemoteScout,
  modifier: Modifier = Modifier,
) {
  val lastSyncText = scout.lastSync.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
  Column(modifier = modifier) {
    Text(
      text = scout.name,
      style = MaterialTheme.typography.bodyLarge
    )
    Text(
      text = stringResource(
        R.string.server_connected_scout_last_sync, lastSyncText),
      style = MaterialTheme.typography.bodyMedium,
      fontStyle = FontStyle.Italic
    )
  }
}