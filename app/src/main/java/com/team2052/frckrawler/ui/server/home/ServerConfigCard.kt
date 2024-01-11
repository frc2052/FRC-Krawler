package com.team2052.frckrawler.ui.server.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.team2052.frckrawler.data.local.Event
import com.team2052.frckrawler.data.local.Game
import com.team2052.frckrawler.ui.components.Card
import com.team2052.frckrawler.ui.components.CardHeader
import com.team2052.frckrawler.ui.components.fields.FRCKrawlerDropdown
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme
import com.team2052.frckrawler.ui.theme.spaceMedium

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
        GamesDropdown(
            configuration = configuration,
            onConfigurationChanged = onConfigurationChanged,
            availableGames = availableGames
        )
        EventDropdown(
            configuration = configuration,
            onConfigurationChanged = onConfigurationChanged,
            availableEvents = availableEvents,
            enabled = configuration.game != null
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
private fun EventDropdown(
    configuration: ServerConfiguration,
    onConfigurationChanged: (ServerConfiguration) -> Unit,
    availableEvents: List<Event>,
    enabled: Boolean,
) {
    var eventValid by remember { mutableStateOf(true) }
    FRCKrawlerDropdown(
        modifier = Modifier.padding(bottom = spaceMedium),
        enabled = enabled,
        value = configuration.event,
        getLabel = { it?.name ?: "" },
        onValueChange = {
            onConfigurationChanged(
                configuration.copy(event = it)
            )
            if (it != null) {
                eventValid = true
            }
        },
        validity = eventValid,
        onFocusChange = { focused ->
            if (!focused) {
                eventValid = (configuration.event != null)
            }
        },
        label = "Event",
        dropdownItems = availableEvents
    )
}

@Composable
private fun GamesDropdown(
    configuration: ServerConfiguration,
    onConfigurationChanged: (ServerConfiguration) -> Unit,
    availableGames: List<Game>
) {
    var gameValid by remember { mutableStateOf(true) }
    FRCKrawlerDropdown(
        value = configuration.game,
        onValueChange = {
            onConfigurationChanged(
                configuration.copy(game = it)
            )
        },
        getLabel = { it?.name ?: "" },
        validity = gameValid,
        onFocusChange = { focused ->
            if (!focused) {
                gameValid = (configuration.game != null)
            }
        },
        label = "Games",
        dropdownItems = availableGames,
    )
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

@Preview
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

