package com.team2052.frckrawler.ui.nav

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.team2052.frckrawler.ui.modeSelect.ModeSelectScreen
import com.team2052.frckrawler.ui.scout.ScoutHomeScreen
import com.team2052.frckrawler.ui.scout.matches.ScoutMatchesScreen
import com.team2052.frckrawler.ui.server.ServerHomeScreen
import com.team2052.frckrawler.ui.server.ServerSeasonsScreen
import com.team2052.frckrawler.ui.server.metrics.MatchMetricsScreen
import com.team2052.frckrawler.ui.server.metrics.PitMetricsScreen
import com.team2052.frckrawler.ui.startup.StartupScreen

/**
 * Main composable for the application, controls navigation
 */
@Composable
fun NavGraph(startNavScreen: NavScreen = NavScreen.SERVER) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = startNavScreen.route,
    ) {
        composable(NavScreen.STARTUP.route) {
            StartupScreen(navController = navController)
        }

        composable(NavScreen.MODE_SELECT.route) {
            ModeSelectScreen(navController = navController)
        }

        navigation(
            startDestination = NavScreen.SCOUT_HOME.route,
            route = NavScreen.SCOUT.route,
        ) {
            composable(NavScreen.SCOUT_HOME.route) {
                ScoutHomeScreen(navController = navController)
            }

            composable(NavScreen.SCOUT_MATCHES.route) {
                ScoutMatchesScreen(navController = navController)
            }
        }

        navigation(
            startDestination = NavScreen.SERVER_HOME.route,
            route = NavScreen.SERVER.route,
        ) {
            composable(NavScreen.SERVER_HOME.route) {
                ServerHomeScreen(navController = navController)
            }

            composable(NavScreen.SERVER_MATCHES.route) {
                // TODO: Implement Server Matches Screen
            }

            composable(NavScreen.SERVER_SEASONS.route) {
                ServerSeasonsScreen(navController = navController)
            }

            navigation(
                startDestination = NavScreen.MATCH_METRICS.route,
                route = NavScreen.METRICS.route,
            ) {
                composable(NavScreen.MATCH_METRICS.route) {
                    MatchMetricsScreen(navController = navController)
                }

                composable(NavScreen.PIT_METRICS.route) {
                    PitMetricsScreen(navController = navController)
                }
            }
        }
    }
}