package com.team2052.frckrawler.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.team2052.frckrawler.ui.startup.StartupScreen
import com.team2052.frckrawler.ui.modeSelect.ModeSelectScreen
import com.team2052.frckrawler.ui.server.home.ServerHomeScreen

/**
 * Contains a loose nav tree responsible for architecting the apps structure
 */
sealed class NavScreen(val route: String, val title: String) {

    object Startup : NavScreen("startup_screen", "startup")

    object ModeSelect : NavScreen("mode_select_screen", "mode select")

    object Scout : NavScreen("scout_screen", "scout") {
        object Home : NavScreen("scout_home_screen", "home")
    }

    object Server : NavScreen("server_screen", "server") {
        object Home : NavScreen("server_home_screen", "home")
        object Matches : NavScreen("server_matches_screen", "matches")
        object Seasons : NavScreen("server_seasons_screen", "seasons")
    }

    override fun toString() = route
}

/**
 * Main composable for the application, controls navigation
 */
@Composable
fun NavGraph(startNavScreen: NavScreen = NavScreen.Startup) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = startNavScreen.route,
    ) {

        composable(NavScreen.Startup.route) {
            StartupScreen(navController = navController)
        }

        composable(NavScreen.ModeSelect.route) {
            ModeSelectScreen(navController = navController)
        }

        navigation(
            startDestination = NavScreen.Scout.Home.route,
            route = NavScreen.Scout.route,
        ) {

            composable(NavScreen.Scout.Home.route) {
                // TODO: Implement Scout Home Screen
            }
        }

        navigation(
            startDestination = NavScreen.Server.Home.route,
            route = NavScreen.Server.route,
        ) {

            composable(NavScreen.Server.Home.route) {
                ServerHomeScreen(navController = navController)
            }

            composable(NavScreen.Server.Matches.route) {
                // TODO: Implement Server Matches Screen
            }

            composable(NavScreen.Server.Seasons.route) {
                // TODO: Implement Server Seasons Screen
            }
        }
    }
}