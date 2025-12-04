package com.team2052.frckrawler.ui.server.home

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.material.color.MaterialColors
import com.team2052.frckrawler.R
import com.team2052.frckrawler.bluetooth.OperationResult
import com.team2052.frckrawler.data.model.RemoteScout
import com.team2052.frckrawler.ui.components.Card
import com.team2052.frckrawler.ui.components.CardHeader
import com.team2052.frckrawler.ui.getResultText
import com.team2052.frckrawler.ui.theme.StaticColors
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Month
import java.time.format.DateTimeFormatter

private val TIME_FORMAT = DateTimeFormatter.ofPattern("h:mm a")

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
    Column {
      scouts.forEach { scout ->
        SyncedScout(scout)
        Spacer(Modifier.height(8.dp))
      }
    }

    Spacer(Modifier.height(8.dp))

    Box(
      modifier = Modifier.fillMaxWidth(),
      contentAlignment = Alignment.BottomEnd
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
    }
  }
}

@Composable
private fun SyncedScout(
  scout: RemoteScout,
  modifier: Modifier = Modifier,
) {
  val lastSyncText = scout.lastSync.format(TIME_FORMAT)
  Column(modifier = modifier) {
    Row(
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = scout.name,
        style = MaterialTheme.typography.bodyLarge
      )

      if (scout.lastSyncResult != OperationResult.Success) {
        Spacer(Modifier.width(4.dp))
        Icon(
          modifier = Modifier.size(20.dp),
          imageVector = Icons.Default.Error,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.error
        )
      }
    }
    if (scout.lastSyncResult != OperationResult.Success) {
      val baseMessage = stringResource(R.string.server_scout_sync_failed)
      val failureText = if (scout.lastSyncResult != OperationResult.Unknown) {
        val reasonMessage = getResultText(scout.lastSyncResult)
        "$baseMessage, $reasonMessage"
      } else {
        baseMessage
      }
      Text(
        text = failureText,
        style = MaterialTheme.typography.bodyMedium,
        fontStyle = FontStyle.Italic,
        color = MaterialTheme.colorScheme.error,
      )
    } else {
      Text(
        text = stringResource(
          R.string.server_connected_scout_last_sync, lastSyncText
        ),
        style = MaterialTheme.typography.bodyMedium,
        fontStyle = FontStyle.Italic
      )
    }
  }
}

@Preview
@Composable
private fun ConnectedScoutListPreview() {
  ConnectedScoutsList(
    scouts = listOf(
      RemoteScout(
        name = "Scout 1",
        address = "AB:CD:EF:12:34:56",
        lastSync = LocalDateTime.of(
          LocalDate.of(2025, Month.MARCH, 10),
          LocalTime.of(8, 52)
        ),
        lastSyncResult = OperationResult.Success
      ),

      RemoteScout(
        name = "Scout 2",
        address = "AB:CD:EF:12:34:56",
        lastSync = LocalDateTime.of(
          LocalDate.of(2025, Month.MARCH, 10),
          LocalTime.of(8, 52)
        ),
        lastSyncResult = OperationResult.VersionMismatch,
      ),

      RemoteScout(
        name = "Scout 3",
        address = "AB:CD:EF:12:34:56",
        lastSync = LocalDateTime.of(
          LocalDate.of(2025, Month.MARCH, 10),
          LocalTime.of(8, 52)
        ),
        lastSyncResult = OperationResult.Unknown
      )
    )
  )
}