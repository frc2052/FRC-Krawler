package com.team2052.frckrawler.ui.server

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Divider
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.team2052.frckrawler.R
import com.team2052.frckrawler.data.local.MetricSet
import com.team2052.frckrawler.ui.components.FRCKrawlerAppBar
import com.team2052.frckrawler.ui.components.FRCKrawlerDrawer
import com.team2052.frckrawler.ui.components.FRCKrawlerScaffold
import com.team2052.frckrawler.ui.components.FRCKrawlerTabBar
import com.team2052.frckrawler.ui.game.AddGameDialog
import com.team2052.frckrawler.ui.navigation.Screen.Metrics
import com.team2052.frckrawler.ui.navigation.Screen.Server
import com.team2052.frckrawler.ui.navigation.Screen.ServerGames
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
            if (viewModel.metricSets.isEmpty()) {
                EmptyBackground()
            }
        }
    ) { _ ->
        GamesList(
            metricSets = viewModel.metricSets,
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
    metricSets: List<MetricSet>,
    onGameClick: (MetricSet) -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        metricSets.forEach { game ->
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
            metricSets = listOf(
                MetricSet(name = "Infinite Recharge"),
                MetricSet(name = "Rapid React"),
                MetricSet(name = "Charged Up"),
            ),
            onGameClick = {}
        )
    }
}