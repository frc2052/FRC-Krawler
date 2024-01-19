package com.team2052.frckrawler.ui.game.list

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import com.team2052.frckrawler.ui.components.FRCKrawlerAppBar
import com.team2052.frckrawler.ui.components.FRCKrawlerScaffold
import com.team2052.frckrawler.ui.game.AddGameDialog
import com.team2052.frckrawler.ui.navigation.Screen
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme

@Composable
fun GameListScreen(
  modifier: Modifier = Modifier,
  navController: NavController,
) {
  val viewModel: GameListViewModel = hiltViewModel()

  var addGameDialogOpen by remember { mutableStateOf(false) }

  LaunchedEffect(true) {
    viewModel.loadGames()
  }

  FRCKrawlerScaffold(
    modifier = modifier,
    appBar = {
      FRCKrawlerAppBar(
        navController = navController,
        title = {
          Text(stringResource(R.string.games_screen_title))
        }
      )
    },
    floatingActionButton = {
      Actions(
        onAddClick = { addGameDialogOpen = true }
      )
    },
    background = {
      if (viewModel.games.isEmpty()) {
        EmptyBackground()
      }
    }
  ) { _ ->
    GameList(
      games = viewModel.games,
      onGameClick = { game -> navController.navigate(Screen.Game(game.id).route) }
    )
    if (addGameDialogOpen) {
      AddGameDialog(
        onAddGame = { newGame -> viewModel.createGame(newGame) },
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
      tint = MaterialTheme.colors.secondary,
      contentDescription = null
    )
    Text(text = "No Games", style = MaterialTheme.typography.h4)
  }
}

@Composable
private fun Actions(onAddClick: () -> Unit) {
  val fabModifier = Modifier.padding(bottom = 24.dp)
  FloatingActionButton(
    modifier = fabModifier,
    onClick = onAddClick
  ) {
    Icon(imageVector = Icons.Filled.Add, contentDescription = "Add new metric set")
  }
}

@Composable
fun GameList(
  modifier: Modifier = Modifier,
  games: List<Game>,
  onGameClick: (Game) -> Unit
) {
  Column(modifier = modifier.fillMaxWidth()) {
    games.forEach { set ->
      Text(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onGameClick(set) }
          .padding(horizontal = 12.dp, vertical = 12.dp),
        text = set.name,
        style = MaterialTheme.typography.h5
      )
      Divider()
    }
  }
}

@FrcKrawlerPreview
@Composable
private fun GameListPreview() {
  FrcKrawlerTheme(darkTheme = false) {
    GameList(
      games = listOf(
        Game(name = "Infinite Recharge"),
        Game(name = "Rapid React"),
        Game(name = "Charged Up"),
      ),
      onGameClick = {}
    )
  }
}