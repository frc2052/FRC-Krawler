package com.team2052.frckrawler.ui.server

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.team2052.frckrawler.data.local.Game
import com.team2052.frckrawler.ui.components.*
import com.team2052.frckrawler.ui.game.AddGameDialog
import com.team2052.frckrawler.ui.navigation.Screen.*
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme

@Composable
fun ServerGamesScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
) {
    val scaffoldState = rememberScaffoldState()
    val viewModel: ServerGamesViewModel = hiltViewModel()

    var addGameDialogOpen by remember { mutableStateOf(false) }

    LaunchedEffect(true) {
        viewModel.loadGames()
    }

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
                onAddClick = { addGameDialogOpen = true }
            )
        },
        drawerContent = {
            FRCKrawlerDrawer()
        },
        background = {
            if (viewModel.games.isEmpty()) {
                EmptyBackground()
            }
        }
    ) { _ ->
        GamesList(
            listOfGames = viewModel.games,
            onGameClick = { game -> navController.navigate(Metrics(game.id).route) }
        )
        if (addGameDialogOpen) {
            AddGameDialog(
                onAddGame = { newGame -> viewModel.makeGame(newGame)},
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
private fun GameActions(onAddClick: () -> Unit) {
    val fabModifier = Modifier.padding(bottom = 24.dp)
    FloatingActionButton(
        modifier = fabModifier,
        onClick = onAddClick
    ) {
        Icon(imageVector = Icons.Filled.Add, contentDescription = "Add new game")
    }
}

@Composable
fun GamesList(
    modifier: Modifier = Modifier,
    listOfGames: List<Game>,
    onGameClick: (Game) -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        listOfGames.forEach { game ->
            Text(
                modifier = Modifier.fillMaxWidth()
                    .clickable { onGameClick(game) }
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                text = game.name,
                style = MaterialTheme.typography.h5
            )
            Divider()
        }
    }
}

@Preview
@Composable
private fun GamesListPreview() {
    FrcKrawlerTheme(darkTheme = false) {
        GamesList(
            listOfGames = listOf(
                Game(name = "Infinite Recharge"),
                Game(name = "Rapid React"),
                Game(name = "Charged Up"),
            ),
            onGameClick = {}
        )
    }
}