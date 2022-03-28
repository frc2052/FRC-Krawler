package com.team2052.frckrawler.ui.server

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.team2052.frckrawler.R
import com.team2052.frckrawler.ui.components.*
import com.team2052.frckrawler.ui.components.fields.FRCKrawlerTextField
import com.team2052.frckrawler.ui.navigation.Screen.*
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme

@Composable
fun ServerGamesScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
) {
    val scaffoldState = rememberScaffoldState()

    var addGameDialogOpen by remember { mutableStateOf(false) }
    var listOfGames by remember { mutableStateOf(emptyList<String>()) }

    FRCKrawlerScaffold(
        modifier = modifier,
        scaffoldState = scaffoldState,
        appBar = {
            FRCKrawlerAppBar(
                navController = navController,
                scaffoldState = scaffoldState,
                title = {
                    Text(stringResource(R.string.server_screen_title))
                }
            )
        },
        tabBar = {
            FRCKrawlerTabBar(navigation = Server, currentScreen = ServerGames) { screen ->
                navController.navigate(screen.route) {
                    popUpTo(ServerGames.route) { inclusive = true }
                    launchSingleTop = true
                }
            }
        },
        floatingActionButton = {
            GameActions(
                onOpen = { addGameDialogOpen = true }
            )
        },
        drawerContent = {
            FRCKrawlerDrawer()
        },
        background = {
            if (listOfGames.isEmpty()) {
                EmptyBackground()
            }
        }
    ) { contentPadding ->
        ServerSeasonsScreenContent(
            modifier = Modifier.padding(contentPadding),
            listOfGames = listOfGames,
            onOpen = {},
            navController = navController
        )
        if (addGameDialogOpen) {
            AddGameDialog(
                onAddGame = { newGame -> listOfGames = listOfGames + newGame },
                onClose = { addGameDialogOpen = false },
            )
        }
    }
}

@Composable
private fun EmptyBackground() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            modifier = Modifier.size(128.dp),
            imageVector = Icons.Filled.Analytics,
            contentDescription = "Background",
            tint = MaterialTheme.colors.secondary,
        )
        Text(text = "No Games", style = MaterialTheme.typography.h4)
    }
}

@Composable
private fun GameActions(onOpen: () -> Unit) {
    var fabExpanded by remember { mutableStateOf(false) }
    Column {
        if (fabExpanded) {
            val fabModifier = Modifier.padding(bottom = 24.dp)
            FloatingActionButton(
                modifier = fabModifier,
                onClick = { onOpen() }
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Manual Season Add")
            }
            FloatingActionButton(
                modifier = fabModifier,
                onClick = {}
            ) {
                Icon(imageVector = Icons.Filled.Delete, contentDescription = "Manual Season Add")
            }
        }
        FloatingActionButton(onClick = {
            fabExpanded = !fabExpanded
        }) {
            if (!fabExpanded) {
                Icon(imageVector = Icons.Filled.Menu, contentDescription = "Manual Season Add")
            } else {
                Icon(imageVector = Icons.Filled.Close, contentDescription = "Manual Season Add")
            }
        }
    }
}

@Composable
private fun AddGameDialog(
    onAddGame: (String) -> Unit,
    onClose: () -> Unit
) {
    var gameName by remember { mutableStateOf("") }
    AlertDialog(
        modifier = Modifier.fillMaxWidth(0.75f),
        onDismissRequest = { onClose() },
        title = { Text("Add New Game") },
        text = {
            FRCKrawlerTextField(
                modifier = Modifier.padding(top = 24.dp),
                value = gameName,
                onValueChange = { gameName = it },
                label = "Name"
            )
        },
        buttons = {
            ProvideTextStyle(LocalTextStyle.current.copy(color = MaterialTheme.colors.secondary)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(
                        modifier = Modifier.padding(12.dp),
                        onClick = {
                            onClose()
                        }
                    ) {
                        Text("CANCEL")
                    }
                    TextButton(
                        modifier = Modifier.padding(12.dp),
                        onClick = {
                            onAddGame(gameName)
                            onClose()
                        }
                    ) {
                        Text("SAVE")
                    }
                }
            }
        }
    )
}

@Composable
fun ServerSeasonsScreenContent(
    modifier: Modifier = Modifier,
    listOfGames: List<String>,
    onOpen:() -> Unit,
    navController: NavController
) {
    Column(modifier = modifier.fillMaxWidth()) {
        listOfGames.forEach { game ->
            TextButton(
                modifier = Modifier.padding(12.dp),
                onClick = {
                    onOpen()
                    navController.navigate(Metrics.route)
                }
            ) {
                Text(
                    text = game,
                    style = MaterialTheme.typography.h4
                )
            }
        }
    }
}

@Preview
@Composable
private fun ServerSeasonsScreenPreviewLight() {
    FrcKrawlerTheme(darkTheme = false) {
        ServerGamesScreen(navController = rememberNavController())
    }
}

@Preview
@Composable
private fun ServerSeasonsScreenPreviewDark() {
    FrcKrawlerTheme(darkTheme = true) {
        ServerGamesScreen(navController = rememberNavController())
    }
}