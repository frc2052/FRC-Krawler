package com.team2052.frckrawler.ui.modeSelect

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import com.team2052.frckrawler.R
import com.team2052.frckrawler.model.Event
import com.team2052.frckrawler.ui.NavScreen
import com.team2052.frckrawler.ui.components.*
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme

/**
 * The mode select screen allows users to choose which mode the app
 * will persist in.
 */
@Composable
fun ModeSelectScreen(
    modifier: Modifier = Modifier,
    viewModel: ModeSelectViewModel = hiltNavGraphViewModel(),
    navController: NavController = rememberNavController(),
) = FRCKrawlerScaffold(
    modifier = modifier,
    currentScreen = NavScreen.ModeSelectScreen
) { contentModifier ->

    Column(
        modifier = contentModifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.h6) {
            Text(stringResource(R.string.welcome_message))
            Text(stringResource(R.string.getting_started_message))
        }
    }

    var expandedCardId by remember { mutableStateOf(-1) }
    FRCKrawlerExpandableCardGroup(
        modifier = contentModifier,
        content = listOf(
            { modifier, id ->
                FRCKrawlerExpandableCard(
                    modifier = modifier,
                    header = {
                        FRCKrawlerCardHeader(
                            title = { Text(stringResource(R.string.mode_remote_scout)) },
                            description = { Text(stringResource(R.string.mode_remote_scout_description)) },
                        )
                    },
                    actions = mapOf(
                        Pair("continue", {
                            navController.navigate(NavScreen.ScoutScreen.route)
                        })
                    ),
                    expanded = expandedCardId == id,
                    onExpanded = { expanded -> expandedCardId = if (expanded) id else -1 },
                )
            }, { modifier, id ->
                FRCKrawlerExpandableCard(
                    modifier = modifier,
                    header = {
                        FRCKrawlerCardHeader(
                            title = { Text(stringResource(R.string.mode_remote_scout)) },
                            description = { Text(stringResource(R.string.mode_remote_scout_description)) },
                        )
                    },
                    actions = mapOf(
                        Pair("continue", {
                            val event = Event.fake()
                            // Push the arguments for the next composable onto the stack before navigating to it
                            // This fixes the problem of not being able to pass objects into the nav controller url
                            navController.currentBackStackEntry?.arguments?.putSerializable(NavScreen.ServerScreen.event, event)
                            navController.navigate(NavScreen.ServerScreen.route)
                        })
                    ),
                    expanded = expandedCardId == id,
                    onExpanded = { expanded -> expandedCardId = if (expanded) id else -1 },
                )
            }, { modifier, id ->
                FRCKrawlerExpandableCard(
                    modifier = modifier,
                    header = {
                        FRCKrawlerCardHeader(
                            title = { Text(stringResource(R.string.mode_remote_scout)) },
                            description = { Text(stringResource(R.string.mode_remote_scout_description)) },
                        )
                    },
                    actions = mapOf(
                        Pair("continue", {
                            navController.navigate(NavScreen.ScoutScreen.route)
                        })
                    ),
                    expanded = expandedCardId == id,
                    onExpanded = { expanded -> expandedCardId = if (expanded) id else -1 },
                )
            },
        )
    )
}

@Preview
@Composable
private fun ModeSelectScreenPreviewLight() {
    FrcKrawlerTheme(darkTheme = false) {
        ModeSelectScreen()
    }
}

@Preview
@Composable
private fun ModeSelectScreenPreviewDark() {
    FrcKrawlerTheme(darkTheme = true) {
        ModeSelectScreen()
    }
}