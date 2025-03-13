package com.team2052.frckrawler.ui.scout.remote

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.team2052.frckrawler.R
import com.team2052.frckrawler.ui.FrcKrawlerPreview
import com.team2052.frckrawler.ui.components.Card
import com.team2052.frckrawler.ui.components.CardHeader
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


@Composable
fun RemoteScoutServerStatusCard(
  modifier: Modifier = Modifier,
  serverState: ServerConnectionState,
  syncState: ServerSyncState,
  onFindServerClicked: () -> Unit,
  onSyncClicked: () -> Unit,
) {
  Card(
    modifier = modifier,
    header = {
      CardHeader(
        title = { Text(stringResource(R.string.scout_server_connection_title)) }
      )
    },
  ) {
    when (serverState) {
      is ServerConnectionState.Connected -> {
        ServerConnected(
          serverState = serverState,
          syncState = syncState,
          onSyncClicked = onSyncClicked,
        )
      }

      is ServerConnectionState.Connecting -> {
        ServerConnecting()
      }

      else -> {
        ServerNotConnected(
          state = serverState,
          onFindServerClicked = onFindServerClicked
        )
      }
    }
  }
}

@Composable
private fun ServerConnected(
  serverState: ServerConnectionState.Connected,
  syncState: ServerSyncState,
  onSyncClicked: () -> Unit,
) {
  Column {
    val connectedText = buildAnnotatedString {
      append(stringResource(R.string.scout_connected_prefix))
      append(" ")
      pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
      append(serverState.name)
    }
    Text(connectedText)

    Spacer(modifier = Modifier.height(16.dp))

    if (syncState is ServerSyncState.Synced && syncState.hasSyncFailure
      || syncState is ServerSyncState.NotSynced && syncState.hasSyncFailure) {
      SyncFailedText()
    }

    if (syncState is ServerSyncState.Synced) {
      LastSyncText(syncState.lastSyncTime)
      Spacer(modifier = Modifier.height(16.dp))
      PendingDataCount(syncState.pendingDataCount)
      Spacer(modifier = Modifier.height(16.dp))
    }

    if (syncState is ServerSyncState.Syncing) {
      Row(
        verticalAlignment = Alignment.CenterVertically
      ) {
        CircularProgressIndicator(
          modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(stringResource(R.string.scout_syncing))
      }

      Spacer(modifier = Modifier.height(16.dp))
    }

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.End
    ) {
      Button(
        onClick = onSyncClicked,
        enabled = syncState != ServerSyncState.Syncing
      ) {
        Icon(
          imageVector = Icons.Default.Sync,
          contentDescription = null
        )
        Spacer(Modifier.width(8.dp))
        Text(stringResource(R.string.scout_sync_action))
      }
    }
  }
}

@Composable
private fun SyncFailedText() {
  Row(
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(
      modifier = Modifier.size(24.dp),
      imageVector = Icons.Default.Error,
      contentDescription = "Failed to sync",
      tint = MaterialTheme.colorScheme.error
    )
    Spacer(Modifier.width(12.dp))
    Text("Sync failed")
  }
}

@Composable
private fun LastSyncText(dateTime: ZonedDateTime) {
  Text(
    text = stringResource(
      id = R.string.scout_last_sync_label,
      dateTime.format(TIME_FORMAT)
    )
  )
}

@Composable
private fun PendingDataCount(count: Int) {
  val pendingCountText = buildAnnotatedString {
    pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
    append(count.toString())
    pop()
    append(" ")
    append(pluralStringResource(R.plurals.scout_unsynced_metrics_label, count, count))
  }
  Text(pendingCountText)
}

@Composable
private fun ServerConnecting() {
  Row {
    CircularProgressIndicator(
      modifier = Modifier.size(28.dp)
    )
    Spacer(modifier = Modifier.width(16.dp))
    Text(stringResource(R.string.scout_server_connecting))
  }
}

@Composable
private fun ServerNotConnected(
  state: ServerConnectionState,
  onFindServerClicked: () -> Unit
) {
  Column {
    Text(stringResource(R.string.remote_scout_connect_to_server))

    when (state) {
      is ServerConnectionState.PairingFailed -> {
        Text(
          modifier = Modifier.padding(vertical = 16.dp),
          color = MaterialTheme.colorScheme.error,
          text = stringResource(R.string.scout_pairing_failed)
        )
      }

      is ServerConnectionState.NoFrcKrawlerServiceFound -> {
        Text(
          modifier = Modifier.padding(vertical = 16.dp),
          color = MaterialTheme.colorScheme.error,
          text = stringResource(R.string.scout_paring_server_not_running)
        )
      }

      else -> {} // No error, so no text needed
    }

    Spacer(modifier = Modifier.height(16.dp))

    Box(
      modifier = Modifier.fillMaxWidth(),
      contentAlignment = Alignment.BottomEnd
    ) {
      Button(
        onClick = onFindServerClicked,
      ) {
        Text(stringResource(R.string.scout_find_server_button))
      }
    }
  }
}

private val TIME_FORMAT = DateTimeFormatter.ofPattern("h:mm a")

@FrcKrawlerPreview
@Composable
private fun ServerStatusConnectedPreview() {
  FrcKrawlerTheme {
    Surface {
      RemoteScoutServerStatusCard(
        serverState = ServerConnectionState.Connected(
          "KnightKrawler Server"
        ),
        syncState = ServerSyncState.NotSynced(hasSyncFailure = false),
        onFindServerClicked = { },
        onSyncClicked = { }
      )
    }
  }
}

@FrcKrawlerPreview
@Composable
private fun ServerStatusNotConnectedPreview() {
  FrcKrawlerTheme {
    Surface {
      RemoteScoutServerStatusCard(
        serverState = ServerConnectionState.NotConnected,
        syncState = ServerSyncState.NotSynced(hasSyncFailure = false),
        onFindServerClicked = { },
        onSyncClicked = { }
      )
    }
  }
}

@FrcKrawlerPreview
@Composable
private fun ServerStatusPendingSyncPreview() {
  FrcKrawlerTheme {
    Surface {
      RemoteScoutServerStatusCard(
        serverState = ServerConnectionState.Connected(
          "KnightKrawler Server"
        ),
        syncState = ServerSyncState.Synced(
          pendingDataCount = 12,
          lastSyncTime = ZonedDateTime.now(),
        ),
        onFindServerClicked = { },
        onSyncClicked = { }
      )
    }
  }
}

@FrcKrawlerPreview
@Composable
private fun ServerStatusSyncingPreview() {
  FrcKrawlerTheme {
    Surface {
      RemoteScoutServerStatusCard(
        serverState = ServerConnectionState.Connected(
          "KnightKrawler Server"
        ),
        syncState = ServerSyncState.Syncing,
        onFindServerClicked = { },
        onSyncClicked = { }
      )
    }
  }
}

@FrcKrawlerPreview
@Composable
private fun ServerStatusFailedSyncPreview() {
  FrcKrawlerTheme {
    Surface {
      RemoteScoutServerStatusCard(
        serverState = ServerConnectionState.Connected(
          "KnightKrawler Server"
        ),
        syncState = ServerSyncState.Synced(
          pendingDataCount = 12,
          lastSyncTime = ZonedDateTime.now(),
          hasSyncFailure = true,
        ),
        onFindServerClicked = { },
        onSyncClicked = { }
      )
    }
  }
}