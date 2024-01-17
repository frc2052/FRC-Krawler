package com.team2052.frckrawler.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.team2052.frckrawler.R
import com.team2052.frckrawler.data.local.Event
import com.team2052.frckrawler.data.local.Game
import com.team2052.frckrawler.ui.FrcKrawlerPreview
import com.team2052.frckrawler.ui.components.fields.FRCKrawlerDropdown
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme
import com.team2052.frckrawler.ui.theme.spaceMedium


@Composable
fun GameAndEventSelector(
    state: GameAndEventState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        GamesDropdown(
            selectedGame = state.selectedGame,
            onGameChanged = { state.selectedGame = it },
            availableGames = state.availableGames
        )
        EventDropdown(
            selectedEvent = state.selectedEvent,
            onEventChanged = { state.selectedEvent = it },
            enabled = state.selectedGame != null && state.availableEvents.isNotEmpty(),
            availableEvents = state.availableEvents
        )

        if (state.selectedGame != null) {
            if (state.selectedGame?.matchMetricsSetId == null && state.selectedGame?.pitMetricsSetId == null) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.game_event_no_metrics_warning),
                    style = MaterialTheme.typography.body2,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colors.error
                )
            }
            if (state.availableEvents.isEmpty()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.game_event_no_events_warning),
                    style = MaterialTheme.typography.body2,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colors.error
                )
            }
        }
    }
}

@Composable
private fun EventDropdown(
    selectedEvent: Event?,
    onEventChanged: (Event?) -> Unit,
    availableEvents: List<Event>,
    enabled: Boolean,
) {
    var eventValid by remember { mutableStateOf(true) }
    FRCKrawlerDropdown(
        modifier = Modifier.padding(bottom = spaceMedium),
        enabled = enabled,
        value = selectedEvent,
        getLabel = { it?.name ?: "" },
        onValueChange = {
            onEventChanged(it)
            if (it != null) {
                eventValid = true
            }
        },
        validity = eventValid,
        onFocusChange = { focused ->
            if (!focused) {
                eventValid = (selectedEvent != null)
            }
        },
        label = stringResource(R.string.game_event_event_label),
        dropdownItems = availableEvents
    )
}

@Composable
private fun GamesDropdown(
    selectedGame: Game?,
    onGameChanged: (Game?) -> Unit,
    availableGames: List<Game>
) {
    var gameValid by remember { mutableStateOf(true) }
    FRCKrawlerDropdown(
        value = selectedGame,
        onValueChange = onGameChanged,
        getLabel = { it?.name ?: "" },
        validity = gameValid,
        onFocusChange = { focused ->
            if (!focused) {
                gameValid = (selectedGame != null)
            }
        },
        enabled = availableGames.isNotEmpty(),
        label = stringResource(R.string.game_event_game_label),
        dropdownItems = availableGames,
    )
}

@Stable
class GameAndEventState {
    var selectedGame: Game? by mutableStateOf(null)
    var availableGames: List<Game> by mutableStateOf(emptyList())
    var selectedEvent: Event? by mutableStateOf(null)
    var availableEvents: List<Event> by mutableStateOf(emptyList())
}

@FrcKrawlerPreview
@Composable
private fun GameAndEventSelectorPreview() {
    val state = GameAndEventState().apply {
        availableEvents = listOf(
            Event(
                name = "10,000 Lakes Regional",
                gameId = 0
            )
        )
        selectedEvent = null
        availableGames = listOf(
            Game(
                name = "Crescendo"
            )
        )
        selectedGame = null
    }
    FrcKrawlerTheme {
        Surface {
            GameAndEventSelector(
                state = state
            )
        }
    }
}