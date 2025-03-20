package com.team2052.frckrawler.ui.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

/**
 * Represents a unique screen consisting of properties for the route, title, and sub-screens
 *
 * TODO refactor to better support arguments in routes
 */
sealed class Screen(
  val route: String,
  val screens: List<Screen> = emptyList(),
  val arguments: List<NamedNavArgument> = emptyList(),
) {

  // Mode selection screen
  data object ModeSelect : Screen("mode_select_screen")

  // Scouting screens
  data object RemoteScoutHome : Screen("scout/remote")

  data class MatchScout(
    val eventId: Int? = null,
    val metricSetId: Int? = null,
  ) : Screen(
    "scout/match/${eventId ?: "{eventId}"}/${metricSetId ?: "{metricSetId}"}",
    arguments = listOf(Arguments.eventId, Arguments.metricSetId)
  )

  data class PitScout(
    val eventId: Int? = null,
    val metricSetId: Int? = null,
  ) : Screen(
    "scout/pit/${eventId ?: "{eventId}"}/${metricSetId ?: "{metricSetId}"}",
    arguments = listOf(Arguments.eventId, Arguments.metricSetId)
  )

  data object GameList : Screen("games" )

  /* TODO clean up this nasty argument business. Args should be non-null */
  data class Server(
    val gameId: Int? = null,
    val eventId: Int? = null,
  ) : Screen(
    "server/${gameId ?: "{gameId}"}/${eventId ?: "{eventId}"}",
    arguments = listOf(Arguments.gameId, Arguments.eventId)
  )

  data class Analyze(
    val gameId: Int? = null,
    val eventId: Int? = null,
  ) : Screen(
    "analyze/${gameId ?: "{gameId}"}/${eventId ?: "{eventId}"}",
    arguments = listOf(Arguments.gameId, Arguments.eventId)
  )

  data class Export(
    val gameId: Int? = null,
    val eventId: Int? = null,
  ) : Screen(
    "export/${gameId ?: "{gameId}"}/${eventId ?: "{eventId}"}",
    arguments = listOf(Arguments.gameId, Arguments.eventId)
  )

  data class Game(val gameId: Int? = null) : Screen(
    route = "game/${gameId ?: "{gameId}"}",
    arguments = listOf(Arguments.gameId)
  )

  data class MetricSet(val metricSetId: Int? = null) : Screen(
    route = "metric_set/${metricSetId ?: "{metricSetId}"}",
    arguments = listOf(Arguments.metricSetId)
  )

  data class Event(val eventId: Int? = null) : Screen(
    route = "event/${eventId ?: "{eventId}"}",
    arguments = listOf(Arguments.eventId)
  )
}

object Arguments {
  val metricSetId = navArgument("metricSetId") { type = NavType.IntType }
  val gameId = navArgument("gameId") { type = NavType.IntType }
  val eventId = navArgument("eventId") { type = NavType.IntType }
}