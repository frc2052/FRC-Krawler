package com.team2052.frckrawler.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator
import com.team2052.frckrawler.ui.analyze.AnalyzeDataScreen
import com.team2052.frckrawler.ui.analyze.team.TeamDataScreen
import com.team2052.frckrawler.ui.event.teams.EventTeamListScreen
import com.team2052.frckrawler.ui.export.ExportDataScreen
import com.team2052.frckrawler.ui.game.detail.GameDetailScreen
import com.team2052.frckrawler.ui.game.list.GameListScreen
import com.team2052.frckrawler.ui.metrics.list.MetricsListScreen
import com.team2052.frckrawler.ui.modeSelect.ModeSelectScreen
import com.team2052.frckrawler.ui.navigation.Screen.Game
import com.team2052.frckrawler.ui.navigation.Screen.GameList
import com.team2052.frckrawler.ui.navigation.Screen.ModeSelect
import com.team2052.frckrawler.ui.navigation.Screen.Server
import com.team2052.frckrawler.ui.scout.match.ScoutMatchScreen
import com.team2052.frckrawler.ui.scout.pit.ScoutPitScreen
import com.team2052.frckrawler.ui.scout.remote.ScoutHomeScreen
import com.team2052.frckrawler.ui.server.home.ServerHomeScreen

private const val transitionDuration = 400

@Composable
fun Navigation() {
  val density = LocalDensity.current
  val slideDistance = remember(density) {
    with(density) { DFEAULT_SLIDE_DISTANCE.roundToPx() }
  }

  val backStack = rememberNavBackStack(ModeSelect)
  
  NavDisplay(
    entryDecorators = listOf(
      rememberSceneSetupNavEntryDecorator(),
      rememberSavedStateNavEntryDecorator(),
      rememberViewModelStoreNavEntryDecorator(),
    ),
    backStack = backStack,
    onBack = { backStack.removeLastOrNull() },
    transitionSpec = {
      sharedAxisEnterX(forward = true, slideDistance = slideDistance) togetherWith
        sharedAxisExitX(forward = true, slideDistance = slideDistance)
    },
    popTransitionSpec = {
      sharedAxisEnterX(forward = false, slideDistance = slideDistance) togetherWith
        sharedAxisExitX(forward = false, slideDistance = slideDistance)
    },
    predictivePopTransitionSpec = {
      sharedAxisEnterX(forward = false, slideDistance = slideDistance) togetherWith
        sharedAxisExitX(forward = false, slideDistance = slideDistance)
    },
    entryProvider = entryProvider {
      entry<Screen.ModeSelect> {
        ModeSelectScreen(backStack = backStack)
      }

      entry<Screen.RemoteScoutHome> {
        ScoutHomeScreen(backStack = backStack)
      }
      
      entry<Screen.MatchScout> { key ->
        ScoutMatchScreen(
          metricSetId = key.metricSetId,
          eventId = key.eventId,
          backStack = backStack
        )
      }

      entry<Screen.PitScout> { key ->
        ScoutPitScreen(
          metricSetId = key.metricSetId,
          eventId = key.eventId,
          backStack = backStack
        )
      }

      entry<Screen.Server> { key ->
        ServerHomeScreen(
          gameId = key.gameId,
          eventId = key.eventId,
          backStack = backStack
        )
      }

      entry<Screen.GameList> {
        GameListScreen(backStack = backStack)
      }

      entry<Screen.Game> { key ->
        GameDetailScreen(
          gameId = key.gameId,
          backStack = backStack
        )
      }

      entry<Screen.Event> { key ->
        EventTeamListScreen(
          eventId = key.eventId,
          backStack = backStack
        )
      }

      entry<Screen.MetricSet> { key ->
        MetricsListScreen(
          metricSetId = key.metricSetId,
          backStack = backStack
        )
      }

      entry<Screen.Analyze> { key ->
        AnalyzeDataScreen(
          gameId = key.gameId,
          eventId = key.eventId,
          backStack = backStack
        )
      }

      entry<Screen.Export> { key ->
        ExportDataScreen(
          gameId = key.gameId,
          eventId = key.eventId,
          backStack = backStack
        )
      }

      entry<Screen.TeamData> { key ->
        TeamDataScreen(
          teamNumber = key.teamNumber,
          backStack = backStack
        )
      }
    }
  )
}