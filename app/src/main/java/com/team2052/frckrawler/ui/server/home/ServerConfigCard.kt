package com.team2052.frckrawler.ui.server.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
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
import com.team2052.frckrawler.ui.theme.onBackgroundLight

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
    if (game != null && event != null) {
      GameState(game)
      EventState(event)
      Spacer(Modifier.height(8.dp))
      ServerState(serverState)
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
private fun GameState(
  game: Game
) {
  val game = buildAnnotatedString {
    pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
    append(stringResource(R.string.server_controls_game_label))
    pop()
    append(" ${game.name}")
  }
  Text(game)
}

@Composable
private fun EventState(
  event: Event,
) {
  val event = buildAnnotatedString {
    pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
    append(stringResource(R.string.server_controls_event_label))
    pop()
    append(" ${event.name}")
  }
  Text(event)
}

@Composable
private fun ServerState(
  serverState: ServerState,
) {
  val stateText = when (serverState) {
    ServerState.ENABLED -> stringResource(R.string.server_controls_server_state_started)
    ServerState.ENABLING -> stringResource(R.string.server_controls_server_state_starting)
    ServerState.DISABLED -> stringResource(R.string.server_controls_server_state_stopped)
    ServerState.DISABLING -> stringResource(R.string.server_controls_server_state_stopping)
  }
  val stateColor = when (serverState) {
    ServerState.ENABLED -> Color(0xFF90C985)
    ServerState.ENABLING, ServerState.DISABLING -> Color(0xFFE7C26C)
    ServerState.DISABLED -> Color(0xFFFFAAAA)
  }
  val stateContentColor = when (serverState) {
    ServerState.ENABLED -> onBackgroundLight
    ServerState.ENABLING, ServerState.DISABLING -> onBackgroundLight
    ServerState.DISABLED -> onBackgroundLight
  }
  Row(
    modifier = Modifier.semantics(mergeDescendants = true) {},
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(
      text = stringResource(R.string.server_controls_server_status_label),
      fontWeight = FontWeight.Bold,
    )
    Spacer(Modifier.width(8.dp))

    Surface(
      color = stateColor,
      contentColor = stateContentColor,
      shape = RoundedCornerShape(4.dp),
    ) {
      Text(
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
        text = stateText
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
  // Disable while transitioning states
  val enabled = serverState == ServerState.ENABLED || serverState == ServerState.DISABLED
  Button(
    modifier = modifier,
    enabled = enabled,
    onClick = toggleServer,
  ) {
    Text(
      when (serverState) {
        ServerState.ENABLED, ServerState.ENABLING ->
          stringResource(R.string.server_controls_server_stop)
        ServerState.DISABLED, ServerState.DISABLING ->
          stringResource(R.string.server_controls_server_start)
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

@FrcKrawlerPreview
@Composable
private fun StateStarted() {
  FrcKrawlerTheme {
    Card {
      ServerState(ServerState.ENABLED)
    }
  }
}

@FrcKrawlerPreview
@Composable
private fun StateStarting() {
  FrcKrawlerTheme {
    Card {
      ServerState(ServerState.ENABLING)
    }
  }
}

@FrcKrawlerPreview
@Composable
private fun StateStopped() {
  FrcKrawlerTheme {
    Card {
      ServerState(ServerState.DISABLED)
    }
  }
}

@FrcKrawlerPreview
@Composable
private fun StateStopping() {
  FrcKrawlerTheme {
    Card {
      ServerState(ServerState.DISABLING)
    }
  }
}



