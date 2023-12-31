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
    object ModeSelect : Screen(
        "mode_select_screen",
        getString(R.string.mode_select_screen_title),
    )

    // Scouting screens
    object Scout : Screen(
        "scout_screen",
        getString(R.string.scout_screen_title),
        listOf(ScoutHome, ScoutMatches),
    )
    object ScoutHome : Screen(
        "scout_home_screen",
        getString(R.string.scout_home_screen_title)
    )
    object ScoutMatches : Screen(
        "scout_matches_screen",
        getString(R.string.scout_matches_screen_title)
    )

    // Server screens
    object Server : Screen(
        "server_screen",
        getString(R.string.server_screen_title),
        listOf(ServerHome, ServerMatches, GameList),
    )
    object ServerHome : Screen(
        "server_home_screen",
        getString(R.string.server_home_screen_title)
    )
    object ServerMatches : Screen(
        "server_matches_screen",
        getString(R.string.server_matches_screen_title)
    )
    object GameList : Screen(
        "games",
        getString(R.string.games_screen_title)
    )

    /* TODO clean up this nasty argument business */
    data class Game(val gameId: Int? = null) : Screen(
        route = "game/${gameId ?: "{gameId}"}",
        title = "",
        arguments = listOf(Arguments.gameId)
    )

    // Metrics screens
    data class MetricSet(val metricSetId: Int? = null) : Screen(
        route = "metric_sets/${metricSetId ?: "{metricSetId}"}/metrics",
        title = getString(R.string.metrics_screen),
        arguments = listOf(Arguments.metricSetId)
    )
}

object Arguments {
    val metricSetId = navArgument("metricSetId") { type = NavType.IntType }
    val gameId = navArgument("gameId") { type = NavType.IntType }
}