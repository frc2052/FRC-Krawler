package com.team2052.frckrawler.ui.navigation

import com.team2052.frckrawler.R
import com.team2052.frckrawler.FRCKrawlerApp.Companion.getString

/**
 * Represents a unique screen consisting of properties for the route, title, and sub-screens
 */
sealed class Screen(
    val route: String,
    val title: String,
    val screens: List<Screen> = emptyList()
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
        listOf(ServerHome, ServerMatches, ServerGames),
    )
    object ServerHome : Screen(
        "server_home_screen",
        getString(R.string.server_home_screen_title)
    )
    object ServerMatches : Screen(
        "server_matches_screen",
        getString(R.string.server_matches_screen_title)
    )
    object ServerGames : Screen(
        "server_games_screen",
        getString(R.string.server_games_screen_title)
    )

    // Metrics screens
    object Metrics : Screen(
        "metrics_screen",
        getString(R.string.metrics_screen),
        listOf(MatchMetrics, PitMetrics),
    )
    object MatchMetrics : Screen(
        "match_metrics_screen",
        getString(R.string.match_metrics_screen)
    )
    object PitMetrics : Screen(
        "pit_metrics_screen",
        getString(R.string.pit_metrics_screen)
    )
}