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
import androidx.compose.ui.unit.dp
import com.team2052.frckrawler.data.local.Event
import com.team2052.frckrawler.data.local.Game
import com.team2052.frckrawler.ui.FrcKrawlerPreview
import com.team2052.frckrawler.ui.components.Card
import com.team2052.frckrawler.ui.components.CardHeader
import com.team2052.frckrawler.ui.components.GameAndEventSelector
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme

@Composable
internal fun ServerConfigCard(
    modifier: Modifier = Modifier,
    availableEvents: List<Event>,
    availableGames: List<Game>,
    configuration: ServerConfiguration,
    onConfigurationChanged: (ServerConfiguration) -> Unit,
    serverState: ServerState,
    toggleServer: () -> Unit,
) {
    Card(
        modifier = modifier,
        header = {
            CardHeader(
                title = { Text("Server Controls") },
                description = { Text("Control server and configuration") },
            )
        },
    ) {
        GameAndEventSelector(
            availableEvents = availableEvents,
            selectedEvent = configuration.event,
            onEventChanged = {
                onConfigurationChanged(
                    configuration.copy(event = it)
                )
            },
            availableGames = availableGames,
            selectedGame = configuration.game,
            onGameChanged = {
                onConfigurationChanged(
                    configuration.copy(game = it)
                )
            },
        )

        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.BottomEnd
        ) {
            ServerToggleButton(
                modifier = modifier,
                serverState = serverState,
                serverConfigurationValid = configuration.isValid,
                toggleServer = toggleServer
            )
        }
    }
}
@Composable
private fun ServerToggleButton(
    modifier: Modifier,
    serverConfigurationValid: Boolean,
    serverState: ServerState,
    toggleServer: () -> Unit
) {
    Button(
        modifier = modifier,
        enabled = serverConfigurationValid && (
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
private fun ServerPropsPreview() {
    FrcKrawlerTheme(darkTheme = false) {
        ServerConfigCard(
            serverState = ServerState.ENABLED,
            toggleServer = {},
            availableEvents = listOf(
                Event(
                    name = "10,000 Lakes Regional",
                    gameId = 0
                )
            ),
            availableGames = listOf(
                Game(
                    name = "Crescendo"
                )
            ),
            configuration = ServerConfiguration(null, null),
            onConfigurationChanged = {}
        )
    }
}

