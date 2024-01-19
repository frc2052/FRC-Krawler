package com.team2052.frckrawler.ui.server.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.team2052.frckrawler.R
import com.team2052.frckrawler.data.local.Event
import com.team2052.frckrawler.data.local.Game
import com.team2052.frckrawler.ui.FrcKrawlerPreview
import com.team2052.frckrawler.ui.components.Card
import com.team2052.frckrawler.ui.components.CardHeader
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme

@Composable
internal fun ServerConfigCard(
  modifier: Modifier = Modifier,
  game: Game?,
  event: Event?,
  serverState: ServerState,
  toggleServer: () -> Unit,
) {
  Card(
    modifier = modifier,
    header = {
      CardHeader(
        title = { Text(stringResource(R.string.server_controls_title)) },
      )
    },
  ) {
    if (game == null || event == null) {
      // TODO do we need a loading state? Hopefully not
    } else {
      val game = buildAnnotatedString {
        pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
        append(stringResource(R.string.server_controls_game_label))
        pop()
        append(" ${game.name}")
      }
      Text(game)

      val event = buildAnnotatedString {
        pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
        append(stringResource(R.string.server_controls_event_label))
        pop()
        append(" ${event.name}")
      }
      Text(event)
    }

    Spacer(Modifier.height(16.dp))

    Box(
      modifier = Modifier.fillMaxWidth(),
      contentAlignment = Alignment.BottomEnd
    ) {
      ServerToggleButton(
        modifier = modifier,
        serverState = serverState,
        toggleServer = toggleServer
      )
    }
  }
}

@Composable
private fun ServerToggleButton(
  modifier: Modifier,
  serverState: ServerState,
  toggleServer: () -> Unit
) {
  Button(
    modifier = modifier,
    enabled = (
            // Disable while transitioning states
            serverState == ServerState.ENABLED || serverState == ServerState.DISABLED
            ),
    onClick = toggleServer,
  ) {
    Text(
      when (serverState) {
        ServerState.ENABLED -> "Stop Server"
        ServerState.ENABLING -> "Starting Server"
        ServerState.DISABLED -> "Start Server"
        ServerState.DISABLING -> "Stopping Server"
      }
    )
  }
}

@FrcKrawlerPreview
@Composable
private fun ServerConfigPreview() {
  FrcKrawlerTheme {
    ServerConfigCard(
      serverState = ServerState.ENABLED,
      toggleServer = {},
      event = Event(
        name = "10,000 Lakes Regional",
        gameId = 0
      ),
      game = Game(
        name = "Crescendo"
      )
    )
  }
}

