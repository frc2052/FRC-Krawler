package com.team2052.frckrawler.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.team2052.frckrawler.ui.event.teams.EventTeamListScreen
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
import timber.log.Timber

private const val transitionOffset = 400
private const val transitionDuration = 400

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Navigation() {
  // The universal navigation controller used for all navigation throughout the app.
  val navController = rememberNavController()

  // Okay, this is super dumb, but back navigation isn't working after rotating the device.
  // For some reason just logging the current backstack state on every navigation fixes it.
  // TODO - definitely come back with a real fix
  LaunchedEffect(true) {
    navController.addOnDestinationChangedListener { controller, destination, arguments ->
      val backStack = controller.currentBackStack.value.joinToString(separator = ",\n\t\t") { entry ->
        "${entry.id}: ${entry.destination.route}"
      }
      Timber.d("NavChange | Destination: $destination\n\tbackstack: $backStack")
    }
  }

  val density = LocalDensity.current
  val slideDistance = remember(density) {
    with(density) { DFEAULT_SLIDE_DISTANCE.roundToPx() }
  }

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
      screen = ModeSelect,
      enterTransition = {
        fadeIn(animationSpec = tween(transitionDuration))
      }
    ) {
      ModeSelectScreen(navController = navController)
    }

    composable(
      screen = Screen.RemoteScoutHome
    ) {
      ScoutHomeScreen(navController = navController)
    }

    composable(
      screen = Screen.MatchScout(),
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
      screen = Screen.PitScout(),
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
      screen = Server(),
    ) { backStackEntry ->
      val gameId = backStackEntry.arguments?.getInt(Arguments.gameId.name) ?: 0
      val eventId = backStackEntry.arguments?.getInt(Arguments.eventId.name) ?: 0
      ServerHomeScreen(
        gameId = gameId,
        eventId = eventId,
        navController = navController
      )
    }

    composable(screen = GameList) {
      GameListScreen(navController = navController)
    }

    composable(
      screen = Game(),
    ) { backStackEntry ->
      val gameId = backStackEntry.arguments?.getInt(Arguments.gameId.name) ?: 0
      GameDetailScreen(gameId = gameId, navController = navController)
    }

    composable(
      screen = Screen.Event(),
    ) { backStackEntry ->
      val eventId = backStackEntry.arguments?.getInt(Arguments.eventId.name) ?: 0
      EventTeamListScreen(eventId = eventId, navController = navController)
    }

    composable(
      screen = Screen.MetricSet(),
    ) { backStackEntry ->
      val metricSetId = backStackEntry.arguments?.getInt(Arguments.metricSetId.name) ?: 0
      MetricsListScreen(metricSetId = metricSetId, navController = navController)
    }
  }
}

// Wrapper function for adding a composable element using the Screen class
@ExperimentalAnimationApi
private fun NavGraphBuilder.composable(
  screen: Screen,
  deepLinks: List<NavDeepLink> = emptyList(),
  enterTransition: (
  AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?
  )? = null,
  exitTransition: (
  AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?
  )? = null,
  popEnterTransition: (
  AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?
  )? = enterTransition,
  popExitTransition: (
  AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?
  )? = exitTransition,
  content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit,
) = composable(
  route = screen.route,
  deepLinks = deepLinks,
  enterTransition = enterTransition,
  exitTransition = exitTransition,
  popEnterTransition = popEnterTransition,
  popExitTransition = popExitTransition,
  arguments = screen.arguments,
  content = content
)