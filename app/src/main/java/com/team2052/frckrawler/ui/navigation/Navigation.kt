package com.team2052.frckrawler.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
  // The universal navigation controller used for all navigation throughout the app.
  val navController = rememberNavController()

  val density = LocalDensity.current
  val slideDistance = remember(density) {
    with(density) { DFEAULT_SLIDE_DISTANCE.roundToPx() }
  }
  println("Main navController: $navController")

  NavHost(
    navController = navController,
    startDestination = ModeSelect.route,
    enterTransition = {
      sharedAxisEnterX(forward = true, slideDistance = slideDistance)
    },
    popEnterTransition = {
      sharedAxisEnterX(forward = false, slideDistance = slideDistance)
    },
    exitTransition = {
      sharedAxisExitX(forward = true, slideDistance = slideDistance)
    },
    popExitTransition = {
      sharedAxisExitX(forward = false, slideDistance = slideDistance)
    }
  ) {
    composable(
      route = ModeSelect.route,
      enterTransition = {
        fadeIn(animationSpec = tween(transitionDuration))
      },
      arguments = ModeSelect.arguments
    ) {
      ModeSelectScreen(navController = navController)
    }

    composable(
      route = Screen.RemoteScoutHome.route,
      arguments = Screen.RemoteScoutHome.arguments
    ) {
      ScoutHomeScreen(navController = navController)
    }

    composable(
      route = Screen.MatchScout().route,
      arguments = Screen.MatchScout().arguments,
    ) { backStackEntry ->
      val metricSetId = backStackEntry.arguments?.getInt(Arguments.metricSetId.name) ?: 0
      val eventId = backStackEntry.arguments?.getInt(Arguments.eventId.name) ?: 0
      ScoutMatchScreen(
        metricSetId = metricSetId,
        eventId = eventId,
        navController = navController
      )
    }

    composable(
      route = Screen.PitScout().route,
      arguments = Screen.PitScout().arguments,
    ) { backStackEntry ->
      val metricSetId = backStackEntry.arguments?.getInt(Arguments.metricSetId.name) ?: 0
      val eventId = backStackEntry.arguments?.getInt(Arguments.eventId.name) ?: 0
      ScoutPitScreen(
        metricSetId = metricSetId,
        eventId = eventId,
        navController = navController
      )
    }

    composable(
      route = Server().route,
      arguments = Server().arguments,
    ) { backStackEntry ->
      val gameId = backStackEntry.arguments?.getInt(Arguments.gameId.name) ?: 0
      val eventId = backStackEntry.arguments?.getInt(Arguments.eventId.name) ?: 0
      ServerHomeScreen(
        gameId = gameId,
        eventId = eventId,
        navController = navController
      )
    }

    composable(
      route = GameList.route,
      arguments = GameList.arguments,
    ) {
      GameListScreen(navController = navController)
    }

    composable(
      route = Game().route,
      arguments = Game().arguments,
    ) { backStackEntry ->
      val gameId = backStackEntry.arguments?.getInt(Arguments.gameId.name) ?: 0
      GameDetailScreen(gameId = gameId, navController = navController)
    }

    composable(
      route = Screen.Event().route,
      arguments = Screen.Event().arguments,
    ) { backStackEntry ->
      val eventId = backStackEntry.arguments?.getInt(Arguments.eventId.name) ?: 0
      EventTeamListScreen(eventId = eventId, navController = navController)
    }

    composable(
      route = Screen.MetricSet().route,
      arguments = Screen.MetricSet().arguments,
    ) { backStackEntry ->
      val metricSetId = backStackEntry.arguments?.getInt(Arguments.metricSetId.name) ?: 0
      MetricsListScreen(metricSetId = metricSetId, navController = navController)
    }

    composable(
      route = Screen.Analyze().route,
      arguments = Screen.Analyze().arguments,
    ) { backStackEntry ->
      val gameId = backStackEntry.arguments?.getInt(Arguments.gameId.name) ?: 0
      val eventId = backStackEntry.arguments?.getInt(Arguments.eventId.name) ?: 0
      AnalyzeDataScreen(
        gameId = gameId,
        eventId = eventId,
        navController = navController
      )
    }

    composable(
      route = Screen.Export().route,
      arguments = Screen.Export().arguments,
    ) { backStackEntry ->
      val gameId = backStackEntry.arguments?.getInt(Arguments.gameId.name) ?: 0
      val eventId = backStackEntry.arguments?.getInt(Arguments.eventId.name) ?: 0
      ExportDataScreen(
        gameId = gameId,
        eventId = eventId,
        navController = navController
      )
    }

    composable(
      route = Screen.TeamData().route,
      arguments = Screen.TeamData().arguments,
    ) { backStackEntry ->
      val teamNumber = backStackEntry.arguments?.getString(Arguments.teamNumber.name) ?: ""
      TeamDataScreen(
        teamNumber = teamNumber,
        navController = navController
      )
    }
  }
}