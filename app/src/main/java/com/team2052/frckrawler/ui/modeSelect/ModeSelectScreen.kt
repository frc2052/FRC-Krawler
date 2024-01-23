package com.team2052.frckrawler.ui.modeSelect

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.RadioButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Aod
import androidx.compose.material.icons.outlined.Hub
import androidx.compose.material.icons.outlined.TapAndPlay
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.team2052.frckrawler.R
import com.team2052.frckrawler.data.local.Game
import com.team2052.frckrawler.data.model.ScoutMode
import com.team2052.frckrawler.ui.FrcKrawlerPreview
import com.team2052.frckrawler.ui.components.Card
import com.team2052.frckrawler.ui.components.CardHeader
import com.team2052.frckrawler.ui.components.ExpandableCard
import com.team2052.frckrawler.ui.components.ExpandableCardGroup
import com.team2052.frckrawler.ui.components.FRCKrawlerAppBar
import com.team2052.frckrawler.ui.components.FRCKrawlerScaffold
import com.team2052.frckrawler.ui.components.GameAndEventSelector
import com.team2052.frckrawler.ui.components.GameAndEventState
import com.team2052.frckrawler.ui.navigation.Screen
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme
import com.team2052.frckrawler.ui.theme.spaceLarge

@Composable
fun ModeSelectScreen(
  modifier: Modifier = Modifier,
  navController: NavController,
) {
  val viewModel: ModeSelectViewModel = hiltViewModel()

  LaunchedEffect(true) {
    viewModel.loadGamesAndEvents()
  }

  FRCKrawlerScaffold(
    modifier = modifier,
    appBar = {
      FRCKrawlerAppBar(
        navController = navController,
        title = {
          Text(stringResource(R.string.mode_select_screen_title))
        }
      )
    },
  ) { contentPadding ->
    ModeSelectScreenContent(
      modifier = modifier.padding(contentPadding),
      isBluetoothAvailable = viewModel.bluetoothAvailability.isAvailable,
      serverGameEventState = viewModel.serverConfigState,
      localScoutGameEventState = viewModel.localScoutConfigState,
      navigate = { screen ->
        navController.navigate(screen.route) {
          popUpTo(Screen.ModeSelect.route)
        }
      }
    )
  }
}

@Composable
private fun ModeSelectScreenContent(
  modifier: Modifier = Modifier,
  isBluetoothAvailable: Boolean,
  serverGameEventState: GameAndEventState,
  localScoutGameEventState: GameAndEventState,
  navigate: (Screen) -> Unit,
) {
  var expandedCard by remember { mutableIntStateOf(-1) }
  val scrollState = rememberScrollState()

  Column(
    modifier = modifier
      .fillMaxWidth()
      .verticalScroll(scrollState)
      .padding(spaceLarge)
  ) {
    ExpandableCardGroup {
      expandableCard { id ->
        RemoteScoutCard(
          expanded = expandedCard == id,
          onExpanded = { expanded -> expandedCard = if (expanded) id else -1 },
          navigate = navigate,
          enabled = isBluetoothAvailable,
        )
      }

      expandableCard { id ->
        ServerCard(
          expanded = expandedCard == id,
          onExpanded = { expanded -> expandedCard = if (expanded) id else -1 },
          gameEventState = serverGameEventState,
          navigate = navigate,
          enabled = isBluetoothAvailable,
        )
      }

      expandableCard { id ->
        SoloScoutCard(
          expanded = expandedCard == id,
          onExpanded = { expanded -> expandedCard = if (expanded) id else -1 },
          gameEventState = localScoutGameEventState,
          navigate = navigate,
        )
      }
    }


    Spacer(modifier = Modifier.height(24.dp))
    Card(
      modifier = Modifier.clickable { navigate(Screen.GameList) },
      header = {
        CardHeader(
          icon = {
            Icon(
              modifier = modifier.size(36.dp),
              imageVector = Icons.Default.Settings,
              contentDescription = null,
            )
          },
          title = { Text(stringResource(R.string.mode_select_configure)) },
          description = { Text(stringResource(R.string.mode_select_configure_description)) },
        )
      },
    )
  }
}

@Composable
private fun RemoteScoutCard(
  expanded: Boolean,
  onExpanded: (Boolean) -> Unit,
  navigate: (Screen) -> Unit,
  enabled: Boolean,
  modifier: Modifier = Modifier,
) {
  ExpandableCard(
    modifier = modifier,
    enabled = enabled,
    header = {
      CardHeader(
        icon = {
          Icon(
            modifier = modifier.size(36.dp),
            imageVector = Icons.Outlined.TapAndPlay,
            contentDescription = null,
          )
        },
        title = { Text(stringResource(R.string.mode_remote_scout)) },
        description = {
          if (enabled) {
            Text(stringResource(R.string.mode_remote_scout_description))
          } else {
            Text(stringResource(R.string.mode_unavailable_no_bluetooth))
          }
       },
      )
    },
    actions = {
      TextButton(onClick = {
        navigate(Screen.RemoteScoutHome)
      }) {
        Text(stringResource(R.string.mode_remote_scout_continue))
      }
    },
    expanded = expanded,
    onExpanded = onExpanded,
    content = {

    },
  )
}

@Composable
private fun ServerCard(
  expanded: Boolean,
  onExpanded: (Boolean) -> Unit,
  gameEventState: GameAndEventState,
  navigate: (Screen) -> Unit,
  enabled: Boolean,
  modifier: Modifier = Modifier,
) {
  ExpandableCard(
    modifier = modifier,
    enabled = enabled,
    header = {
      CardHeader(
        icon = {
          Icon(
            modifier = modifier.size(36.dp),
            imageVector = Icons.Outlined.Hub,
            contentDescription = null,
          )
        },
        title = { Text(stringResource(R.string.mode_server)) },
        description = {
          if (enabled) {
            Text(stringResource(R.string.mode_server_description))
          } else {
            Text(stringResource(R.string.mode_unavailable_no_bluetooth))
          }
        },
      )
    },
    actions = {
      TextButton(
        onClick = {
          navigate(
            Screen.Server(
              gameId = gameEventState.selectedGame!!.id,
              eventId = gameEventState.selectedEvent!!.id,
            )
          )
        },
        enabled = gameEventState.selectedGame != null && gameEventState.selectedEvent != null
      ) {
        Text(stringResource(R.string.mode_server_continue))
      }
    },
    expanded = expanded,
    onExpanded = onExpanded,
    content = {
      GameAndEventSelector(
        state = gameEventState
      )
    },
  )
}

@Composable
private fun SoloScoutCard(
  expanded: Boolean,
  onExpanded: (Boolean) -> Unit,
  gameEventState: GameAndEventState,
  navigate: (Screen) -> Unit,
  modifier: Modifier = Modifier,
) {
  var scoutMode by remember { mutableStateOf(ScoutMode.Match) }
  ExpandableCard(
    modifier = modifier,
    header = {
      CardHeader(
        icon = {
          Icon(
            modifier = modifier.size(36.dp),
            imageVector = Icons.Outlined.Aod,
            contentDescription = null,
          )
        },
        title = { Text(stringResource(R.string.mode_solo_scout)) },
        description = { Text(stringResource(R.string.mode_solo_scout_description)) },
      )
    },
    actions = {
      TextButton(
        onClick = {
          when (scoutMode) {
            ScoutMode.Match -> {
              navigate(
                Screen.MatchScout(
                  metricSetId = gameEventState.selectedGame!!.matchMetricsSetId,
                  eventId = gameEventState.selectedEvent!!.id
                )
              )
            }

            ScoutMode.Pit -> {
              navigate(
                Screen.PitScout(
                  metricSetId = gameEventState.selectedGame!!.pitMetricsSetId,
                  eventId = gameEventState.selectedEvent!!.id
                )
              )
            }
          }
        },
        enabled = gameEventState.selectedGame != null && gameEventState.selectedEvent != null
      ) {
        Text(stringResource(R.string.mode_solo_scout_continue))
      }
    },
    expanded = expanded,
    onExpanded = onExpanded,
    content = {
      GameAndEventSelector(
        state = gameEventState
      )
      Spacer(Modifier.height(4.dp))
      ScoutModeRadioGroup(
        mode = scoutMode,
        onModeChanged = { scoutMode = it },
        matchEnabled = gameEventState.selectedGame?.matchMetricsSetId != null,
        pitEnabled = gameEventState.selectedGame?.pitMetricsSetId != null,
      )
    },
  )
}

@Composable
private fun ScoutModeRadioGroup(
  mode: ScoutMode,
  onModeChanged: (ScoutMode) -> Unit,
  matchEnabled: Boolean,
  pitEnabled: Boolean,
  modifier: Modifier = Modifier,
) {
  Row {
    Row(
      modifier = modifier
        .selectable(
          onClick = { onModeChanged(ScoutMode.Match) },
          enabled = matchEnabled,
          selected = mode == ScoutMode.Match && matchEnabled
        )
        .padding(4.dp)
    ) {
      RadioButton(
        selected = mode == ScoutMode.Match && matchEnabled,
        enabled = matchEnabled,
        onClick = null
      )
      Spacer(Modifier.width(4.dp))
      Text(
        text = stringResource(R.string.mode_select_scout_match),
        color = if (matchEnabled) {
          LocalContentColor.current
        } else {
          LocalContentColor.current.copy(alpha = ContentAlpha.disabled)
        })
    }

    Spacer(Modifier.width(8.dp))

    Row(
      modifier = modifier
          .selectable(
              selected = mode == ScoutMode.Pit && pitEnabled,
              enabled = pitEnabled,
              onClick = { onModeChanged(ScoutMode.Pit) }
          )
          .padding(4.dp)
    ) {
      RadioButton(
        selected = mode == ScoutMode.Pit && pitEnabled,
        enabled = pitEnabled,
        onClick = null
      )
      Spacer(Modifier.width(4.dp))
      Text(
        text = stringResource(R.string.mode_select_scout_pit),
        color = if (pitEnabled) {
          LocalContentColor.current
        } else {
          LocalContentColor.current.copy(alpha = ContentAlpha.disabled)
        }
      )
    }
  }
}

@FrcKrawlerPreview
@Composable
private fun ModeSelectScreenPreviewLight() {
  val gameEventState = GameAndEventState().apply {
    availableGames = listOf(
      Game(name = "Crescendo")
    )
  }
  FrcKrawlerTheme {
    ModeSelectScreenContent(
      serverGameEventState = gameEventState,
      localScoutGameEventState = gameEventState,
      isBluetoothAvailable = true,
      navigate = {}
    )
  }
}

@FrcKrawlerPreview
@Composable
private fun RemoteScoutCardPreview() {
  FrcKrawlerTheme {
    Surface {
      RemoteScoutCard(
        expanded = true,
        onExpanded = {},
        navigate = {},
        enabled = true,
      )
    }
  }
}

@FrcKrawlerPreview
@Composable
private fun ServerCardPreview() {
  val gameEventState = GameAndEventState().apply {
    availableGames = listOf(
      Game(name = "Crescendo")
    )
  }
  FrcKrawlerTheme {
    Surface {
      ServerCard(
        expanded = true,
        onExpanded = {},
        gameEventState = gameEventState,
        navigate = {},
        enabled = true,
      )
    }
  }
}

@FrcKrawlerPreview
@Composable
private fun LocalScoutCardPreview() {
  val gameEventState = GameAndEventState().apply {
    availableGames = listOf(
      Game(name = "Crescendo")
    )
  }
  FrcKrawlerTheme {
    Surface {
      SoloScoutCard(
        expanded = true,
        onExpanded = {},
        gameEventState = gameEventState,
        navigate = {}
      )
    }
  }
}