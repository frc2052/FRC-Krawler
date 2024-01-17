package com.team2052.frckrawler.ui.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.team2052.frckrawler.FRCKrawlerApp.Companion.getString
import com.team2052.frckrawler.R

/**
 * Represents a unique screen consisting of properties for the route, title, and sub-screens
 *
 * TODO refactor to better support arguments in routes
 */
sealed class Screen(
    val route: String,
    val title: String,
    val screens: List<Screen> = emptyList(),
    val arguments: List<NamedNavArgument> = emptyList(),
) {

    // Mode selection screen
    data object ModeSelect : Screen(
        "mode_select_screen",
        getString(R.string.mode_select_screen_title),
    )

    // Scouting screens
    data object Scout : Screen(
        "scout_screen",
        getString(R.string.scout_screen_title),
        listOf(ScoutHome),
    )
    data object ScoutHome : Screen(
        "scout_home_screen",
        getString(R.string.scout_home_screen_title)
    )
    data object ScoutMatches : Screen(
        "scout_matches_screen",
        getString(R.string.scout_matches_screen_title)
    )

    data object GameList : Screen(
        "games",
        getString(R.string.games_screen_title)
    )

    /* TODO clean up this nasty argument business. Args should be non-null */
    data class Server(
        val gameId: Int? = null,
        val eventId: Int? = null,
    ) : Screen(
        "server/${gameId ?: "{gameId}"}/${eventId ?: "{eventId}"}",
        getString(R.string.server_screen_title),
        arguments = listOf(Arguments.gameId, Arguments.eventId)
    )

    data class Game(val gameId: Int? = null) : Screen(
        route = "game/${gameId ?: "{gameId}"}",
        title = "",
        arguments = listOf(Arguments.gameId)
    )

    data class MetricSet(val metricSetId: Int? = null) : Screen(
        route = "metric_set/${metricSetId ?: "{metricSetId}"}",
        title = getString(R.string.metrics_screen),
        arguments = listOf(Arguments.metricSetId)
    )

    data class Event(val eventId: Int? = null) : Screen(
        route = "event/${eventId ?: "{eventId}"}",
        title = "",
        arguments = listOf(Arguments.eventId)
    )
}

object Arguments {
    val metricSetId = navArgument("metricSetId") { type = NavType.IntType }
    val gameId = navArgument("gameId") { type = NavType.IntType }
    val eventId = navArgument("eventId") { type = NavType.IntType }
}