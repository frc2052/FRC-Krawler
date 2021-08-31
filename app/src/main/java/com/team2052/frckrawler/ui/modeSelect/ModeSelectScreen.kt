package com.team2052.frckrawler.ui.modeSelect

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.team2052.frckrawler.R
import com.team2052.frckrawler.ui.nav.Screen
import com.team2052.frckrawler.ui.components.*
import com.team2052.frckrawler.ui.components.fields.FRCKrawlerDropdown
import com.team2052.frckrawler.ui.components.fields.FRCKrawlerTextField
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme
import com.team2052.frckrawler.ui.theme.spaceMedium

@Composable
fun ModeSelectScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
) {
    val viewModel: ModeSelectViewModel = hiltViewModel()
    val scaffoldState = rememberScaffoldState()

    FRCKrawlerScaffold(
        modifier = modifier,
        scaffoldState = scaffoldState,
        refreshing = viewModel.isRefreshing,
        onRefresh = { viewModel.refresh() },
        appBar = {
            FRCKrawlerAppBar(
                navController = navController,
                scaffoldState = scaffoldState,
                title = {
                    Text(stringResource(R.string.mode_select_screen_title))
                }
            )
        },
        drawerContent = {
            FRCKrawlerDrawer()
        },
    ) { contentPadding ->
        ModeSelectScreenContent(
            modifier = modifier.padding(contentPadding),
            viewModel = viewModel,
            navController = navController,
        )
    }
}

@Composable
private fun ModeSelectScreenContent(
    modifier: Modifier = Modifier,
    viewModel: ModeSelectViewModel,
    navController: NavController,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ProvideTextStyle(MaterialTheme.typography.h6) {
            Text(stringResource(R.string.welcome_message))
            Text(stringResource(R.string.getting_started_message))
        }
    }

    // TODO: Hoisting the state to the view model may be unnecessary if we use rememberSaveable
    FRCKrawlerExpandableCardGroup(modifier = modifier) {
        listOf(
            { modifier, id ->
                var game by remember { mutableStateOf(viewModel.remoteScoutData.game) }
                var event by remember { mutableStateOf(viewModel.remoteScoutData.event) }

                FRCKrawlerExpandableCard(
                    modifier = modifier,
                    header = {
                        FRCKrawlerCardHeader(
                            title = { Text(stringResource(R.string.mode_remote_scout)) },
                            description = { Text(stringResource(R.string.mode_remote_scout_description)) },
                        )
                    },
                    actions = {
                        TextButton(onClick = {
                            if (game.isNotBlank() && event.isNotBlank()) {
                                navController.navigate(Screen.Scout.route) {
                                    popUpTo(Screen.ModeSelect.route) { inclusive = true }
                                }
                            }
                        }) {
                            Text(stringResource(R.string.mode_remote_scout_continue))
                        }
                    },
                    expanded = viewModel.expandedCard == id,
                    onExpanded = { expanded -> viewModel.expandedCard = if (expanded) id else -1 },
                    content = {
                        FRCKrawlerDropdown(
                            modifier = Modifier.padding(bottom = 12.dp),
                            value = game,
                            onValueChange = {
                                game = it
                                viewModel.remoteScoutData = viewModel.remoteScoutData.copy(game = game)
                            },
                            label = "Game",
                            dropdownItems = listOf("Infinite Recharge at Home", "Infinite Recharge", "Rover Ruckus")
                        )

                        FRCKrawlerDropdown(
                            value = event,
                            onValueChange = {
                                event = it
                                viewModel.remoteScoutData = viewModel.remoteScoutData.copy(event = event)
                            },
                            label = "Event",
                            dropdownItems = listOf("Northern Lights")
                        )
                    },
                )
            },
            { modifier, id ->
                var teamNumber by remember { mutableStateOf(viewModel.serverData.teamNumber) }
                var teamNumberValidity by remember { mutableStateOf(true) }
                val teamNumberLength = 4

                var serverName by remember { mutableStateOf(viewModel.serverData.serverName) }
                var serverNameValidity by remember { mutableStateOf(true) }
                val serverNameLengthRange = IntRange(4, 20)

                // TODO: In the future using something other than strings, which could be unreliable, could be good
                var game by remember { mutableStateOf(viewModel.serverData.game) }
                var gameValidity by remember { mutableStateOf(true) }

                var event by remember { mutableStateOf(viewModel.serverData.event) }
                var eventValidity by remember { mutableStateOf(true) }

                FRCKrawlerExpandableCard(
                    modifier = modifier,
                    header = {
                        FRCKrawlerCardHeader(
                            title = { Text(stringResource(R.string.mode_server)) },
                            description = { Text(stringResource(R.string.mode_server_description)) },
                        )
                    },
                    actions = {
                        TextButton(onClick = {
                            if (teamNumber.length != teamNumberLength) teamNumberValidity = false
                            if (serverName.length !in serverNameLengthRange) serverNameValidity = false
                            if (game.isEmpty()) gameValidity = false
                            if (event.isEmpty()) eventValidity = false

                            if (teamNumberValidity && serverNameValidity && gameValidity && eventValidity) {
                                navController.navigate(Screen.Server.route) {
                                    popUpTo(Screen.ModeSelect.route) { inclusive = true }
                                }
                            }
                        }) {
                            Text(stringResource(R.string.mode_server_continue))
                        }
                    },
                    expanded = viewModel.expandedCard == id,
                    onExpanded = { expanded -> viewModel.expandedCard = if (expanded) id else -1 },
                    content = {
                        // Team number text field
                        FRCKrawlerTextField(
                            modifier = Modifier.padding(bottom = spaceMedium),
                            value = teamNumber,
                            onValueChange = { value ->
                                if (value.length <= teamNumberLength) teamNumber = value
                                viewModel.serverData = viewModel.serverData.copy(teamNumber = teamNumber)
                            },
                            validity = teamNumberValidity,
                            validityCheck = { value ->
                                teamNumberValidity = value.isDigitsOnly() && value.length == teamNumberLength // && !value.contains("(bad|word|filter|hope|works)")
                            },
                            isError = { validity -> !validity },
                            label = "Team Number",
                            keyboardType = KeyboardType.NumberPassword,
                        )

                        // Server name text field
                        var serverNameHasFocus by remember { mutableStateOf(false) }
                        FRCKrawlerTextField(
                            modifier = Modifier.padding(bottom = spaceMedium),
                            value = serverName,
                            onValueChange = { value ->
                                if (value.length <= serverNameLengthRange.last) serverName = value
                                serverNameValidity = value.length in serverNameLengthRange

                                viewModel.serverData = viewModel.serverData.copy(serverName = serverName)
                            },
                            validity = serverNameValidity,
                            validityCheck = { value ->
                                serverNameValidity = value.length in serverNameLengthRange // && !value.contains("(bad|word|filter|hope|works)")
                            },
                            isError = { validity -> !validity },
                            label = "Server Name" + if (serverNameHasFocus) " - ${serverName.length}/${serverNameLengthRange.last}" else "",
                            onFocusChange = { serverNameHasFocus = it },
                        )

                        // Game selection dropdown
                        FRCKrawlerDropdown(
                            modifier = Modifier.padding(bottom = 12.dp),
                            value = game,
                            onValueChange = {
                                game = it
                                viewModel.serverData = viewModel.serverData.copy(game = game)
                            },
                            validity = gameValidity,
                            validityCheck = {
                                gameValidity = it.isNotEmpty()
                            },
                            label = "Game",
                            dropdownItems = listOf("Infinite Recharge at Home", "Infinite Recharge", "Rover Ruckus")
                        )

                        // Event selection dropdown
                        FRCKrawlerDropdown(
                            value = event,
                            onValueChange = {
                                event = it
                                viewModel.serverData = viewModel.serverData.copy(event = event)
                            },
                            validity = eventValidity,
                            validityCheck = {
                                eventValidity = it.isNotEmpty()
                            },
                            label = "Event",
                            dropdownItems = listOf("Northern Lights")
                        )
                    },
                )
            },
            { modifier, id ->
                var game by remember { mutableStateOf(viewModel.soloScoutData.game) }
                var event by remember { mutableStateOf(viewModel.soloScoutData.event) }

                FRCKrawlerExpandableCard(
                    modifier = modifier,
                    header = {
                        FRCKrawlerCardHeader(
                            title = { Text(stringResource(R.string.mode_solo_scout)) },
                            description = { Text(stringResource(R.string.mode_solo_scout_description)) },
                        )
                    },
                    actions = {
                        TextButton(onClick = {
                            if (game.isNotBlank() && event.isNotBlank()) {
                                navController.navigate(Screen.Scout.route) {
                                    popUpTo(Screen.ModeSelect.route) { inclusive = true }
                                }
                            }
                        }) {
                            Text(stringResource(R.string.mode_solo_scout_continue))
                        }
                    },
                    expanded = viewModel.expandedCard == id,
                    onExpanded = { expanded -> viewModel.expandedCard = if (expanded) id else -1 },
                    content = {
                        FRCKrawlerDropdown(
                            modifier = Modifier.padding(bottom = 12.dp),
                            value = game,
                            onValueChange = {
                                game = it
                                viewModel.soloScoutData = viewModel.soloScoutData.copy(game = game)
                            },
                            label = "Game",
                            dropdownItems = listOf("Infinite Recharge at Home", "Infinite Recharge", "Rover Ruckus")
                        )

                        FRCKrawlerDropdown(
                            value = event,
                            onValueChange = {
                                event = it
                                viewModel.soloScoutData = viewModel.soloScoutData.copy(event = event)
                            },
                            label = "Event",
                            dropdownItems = listOf("Northern Lights")
                        )
                    },
                )
            },
        )
    }
}

@Preview
@Composable
private fun ModeSelectScreenPreviewLight() {
    FrcKrawlerTheme(darkTheme = false) {
        ModeSelectScreen(navController = rememberNavController())
    }
}

@Preview
@Composable
private fun ModeSelectScreenPreviewDark() {
    FrcKrawlerTheme(darkTheme = true) {
        ModeSelectScreen(navController = rememberNavController())
    }
}