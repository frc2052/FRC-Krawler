package com.team2052.frckrawler.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.team2052.frckrawler.model.Event
import com.team2052.frckrawler.ui.startup.StartupScreen
import com.team2052.frckrawler.ui.modeSelect.ModeSelectScreen
import com.team2052.frckrawler.ui.modeSelect.ModeSelectViewModel
import com.team2052.frckrawler.ui.scout.ScoutScreen
import com.team2052.frckrawler.ui.server.ServerScreen

/**
 * Main composable for the application, controls navigation
 */
@Composable
fun FRCKrawlerMain(
    modifier: Modifier = Modifier,
    startDestination: NavScreen = NavScreen.SplashScreen,
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination.route
    ) {
        composable(
            route = NavScreen.SplashScreen.route
        ) { backStackEntry ->
            StartupScreen(modifier = modifier, navController = navController)
        }

        composable(
            route = NavScreen.ModeSelectScreen.route
        ) { backStackEntry ->
            val viewModel: ModeSelectViewModel = hiltNavGraphViewModel(backStackEntry = backStackEntry)
            
            ModeSelectScreen(modifier = modifier, viewModel = viewModel, navController = navController)
        }

        composable(
            route = NavScreen.ScoutScreen.route
        ) { backStackEntry ->
            ScoutScreen(modifier = modifier, navController = navController)
        }

        composable(
            route = NavScreen.ServerScreen.route,
            // TODO: this code crashes the app
//            arguments = listOf(
//                navArgument(NavScreen.ServerScreen.event) { type = NavType.SerializableType(Event::class.java) }
//            )
        ) { backStackEntry ->

            // TODO: Change to ServerViewModel
            val viewModel: ModeSelectViewModel = hiltNavGraphViewModel(backStackEntry = backStackEntry)

            val event =
                navController.previousBackStackEntry?.arguments?.getSerializable(
                    NavScreen.ServerScreen.event
                ) as Event? ?: return@composable

            ServerScreen(modifier = modifier, navController = navController, event = event)
        }
    }
}