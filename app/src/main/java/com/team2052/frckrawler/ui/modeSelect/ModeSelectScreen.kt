package com.team2052.frckrawler.ui.modeSelect

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.team2052.frckrawler.R
import com.team2052.frckrawler.data.local.Game
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
    serverGameEventState: GameAndEventState,
    localScoutGameEventState: GameAndEventState,
    navigate: (Screen) -> Unit,
) {
    var expandedCard by remember { mutableIntStateOf(-1) }
    val scrollState = rememberScrollState()

    Column(modifier = modifier
        .fillMaxWidth()
        .verticalScroll(scrollState)
        .padding(spaceLarge)) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ProvideTextStyle(MaterialTheme.typography.h6) {
                Text(stringResource(R.string.welcome_message))
                Text(stringResource(R.string.getting_started_message))
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        ExpandableCardGroup {
            expandableCard { id ->
                RemoteScoutCard(
                    expanded = expandedCard == id,
                    onExpanded = { expanded -> expandedCard = if (expanded) id else -1 },
                    navigate = navigate,
                )
            }

            expandableCard { id ->
                ServerCard(
                    expanded = expandedCard == id,
                    onExpanded = { expanded -> expandedCard = if (expanded) id else -1 },
                    gameEventState = serverGameEventState,
                    navigate = navigate,
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
    modifier: Modifier = Modifier,
) {
    ExpandableCard(
        modifier = modifier,
        header = {
            CardHeader(
                title = { Text(stringResource(R.string.mode_remote_scout)) },
                description = { Text(stringResource(R.string.mode_remote_scout_description)) },
            )
        },
        actions = {
            TextButton(onClick = {
                navigate(Screen.Scout)
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
    modifier: Modifier = Modifier,
) {
    ExpandableCard(
        modifier = modifier,
        header = {
            CardHeader(
                title = { Text(stringResource(R.string.mode_server)) },
                description = { Text(stringResource(R.string.mode_server_description)) },
            )
        },
        actions = {
            TextButton(
                onClick = { navigate(Screen.Server()) },
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
    ExpandableCard(
        modifier = modifier,
        header = {
            CardHeader(
                title = { Text(stringResource(R.string.mode_solo_scout)) },
                description = { Text(stringResource(R.string.mode_solo_scout_description)) },
            )
        },
        actions = {
            TextButton(
                onClick = { navigate(Screen.Scout) },
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
        },
    )
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
                navigate = {}
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
                navigate = {}
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