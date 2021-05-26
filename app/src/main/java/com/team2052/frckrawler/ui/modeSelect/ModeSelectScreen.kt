package com.team2052.frckrawler.ui.modeSelect

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.team2052.frckrawler.R
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
    navController: NavController = rememberNavController(),
    currentNavScreen: NavScreen = NavScreen.ModeSelect,
) {
    val scaffoldState = rememberScaffoldState()

    FRCKrawlerScaffold(
        modifier = modifier,
        scaffoldState = scaffoldState,
        appBar = {
            FRCKrawlerAppBar(navController = navController, title = {
                Text(stringResource(R.string.mode_select_screen_title))
            })
        }
    ) { contentPadding ->

        val viewModel: ModeSelectViewModel = hiltViewModel()
        viewModel.headerTest()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ProvideTextStyle(MaterialTheme.typography.h6) {
                Text(stringResource(R.string.welcome_message))
                Text(stringResource(R.string.getting_started_message))
            }
        }

        var expandedCard by remember { mutableStateOf(-1) }
        FRCKrawlerExpandableCardGroup(modifier = Modifier.padding(contentPadding)) {
            listOf(
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
                            Pair(stringResource(R.string.mode_remote_scout_continue), {
                                navController.navigate(NavScreen.Scout.route) {
                                    popUpTo(currentNavScreen.route) { inclusive = true }
                                }
                            })
                        ),
                        expanded = expandedCard == id,
                        onExpanded = { expanded -> expandedCard = if (expanded) id else -1 },
                    )
                },
                { modifier, id ->
                    FRCKrawlerExpandableCard(
                        modifier = modifier,
                        header = {
                            FRCKrawlerCardHeader(
                                title = { Text(stringResource(R.string.mode_server)) },
                                description = { Text(stringResource(R.string.mode_server_description)) },
                            )
                        },
                        actions = mapOf(
                            Pair(stringResource(R.string.mode_server_continue), {
                                navController.navigate(NavScreen.Server.route) {
                                    popUpTo(currentNavScreen.route) { inclusive = true }
                                }
                            })
                        ),
                        expanded = expandedCard == id,
                        onExpanded = { expanded -> expandedCard = if (expanded) id else -1 },
                    )
                },
                { modifier, id ->
                    FRCKrawlerExpandableCard(
                        modifier = modifier,
                        header = {
                            FRCKrawlerCardHeader(
                                title = { Text(stringResource(R.string.mode_solo_scout)) },
                                description = { Text(stringResource(R.string.mode_solo_scout_description)) },
                            )
                        },
                        actions = mapOf(
                            Pair(stringResource(R.string.mode_solo_scout_continue), {
                                navController.navigate(NavScreen.Scout.route) {
                                    popUpTo(currentNavScreen.route) { inclusive = true }
                                }
                            })
                        ),
                        expanded = expandedCard == id,
                        onExpanded = { expanded -> expandedCard = if (expanded) id else -1 },
                    )
                },
            )
        }
    }
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