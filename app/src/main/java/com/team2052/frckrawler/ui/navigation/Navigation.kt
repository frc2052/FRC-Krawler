package com.team2052.frckrawler.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.team2052.frckrawler.data.local.MetricCategory
import com.team2052.frckrawler.ui.metrics.list.MetricsListScreen
import com.team2052.frckrawler.ui.modeSelect.ModeSelectScreen
import com.team2052.frckrawler.ui.navigation.Screen.MatchMetrics
import com.team2052.frckrawler.ui.navigation.Screen.MetricSets
import com.team2052.frckrawler.ui.navigation.Screen.Metrics
import com.team2052.frckrawler.ui.navigation.Screen.ModeSelect
import com.team2052.frckrawler.ui.navigation.Screen.PitMetrics
import com.team2052.frckrawler.ui.navigation.Screen.Scout
import com.team2052.frckrawler.ui.navigation.Screen.ScoutHome
import com.team2052.frckrawler.ui.navigation.Screen.ScoutMatches
import com.team2052.frckrawler.ui.navigation.Screen.Server
import com.team2052.frckrawler.ui.navigation.Screen.ServerHome
import com.team2052.frckrawler.ui.navigation.Screen.ServerMatches
import com.team2052.frckrawler.ui.scout.ScoutHomeScreen
import com.team2052.frckrawler.ui.scout.ScoutMatchesScreen
import com.team2052.frckrawler.ui.metric_set.MetricSetListScreen
import com.team2052.frckrawler.ui.server.ServerMatchesScreen
import com.team2052.frckrawler.ui.server.home.ServerHomeScreen

private const val transitionOffset = 400
private const val transitionDuration = 400

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Navigation(initialScreen: Screen = ModeSelect) {
    // The universal navigation controller used for all navigation throughout the app.
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = initialScreen.route,
        enterTransition = {
            EnterTransition.None
        },
        exitTransition = {
            ExitTransition.None
        },
    ) {
        composable(
            screen = ModeSelect,
            enterTransition = {
                // Check if mode select is the first screen and run the startup animation accordingly.
                if (initialState.destination.route == null) {
                    fadeIn(animationSpec = tween(transitionDuration))
                } else {
                    slideInHorizontally(
                        initialOffsetX = { -transitionOffset },
                        animationSpec = tween(transitionDuration)
                    ) + fadeIn(animationSpec = tween(transitionDuration))
                }
            }
        ) {
            ModeSelectScreen(navController = navController)
        }

        navigation(
            navigation = Scout,
            initialScreen = ScoutHome,
        ) {
            composable(screen = ScoutHome) {
                ScoutHomeScreen(navController = navController)
            }

            composable(screen = ScoutMatches) {
                ScoutMatchesScreen(navController = navController)
            }
        }

        /**
         * Currently the animated nav-graph doesn't look good when navigating between
         * tabs. TODO: Implement shared element transitions when they come out
         */

        /**
         * Currently the animated nav-graph doesn't look good when navigating between
         * tabs. TODO: Implement shared element transitions when they come out
         */
        navigation(
            navigation = Server,
            initialScreen = ServerHome,
        ) {
            composable(screen = ServerHome) {
                ServerHomeScreen(navController = navController)
            }

            composable(screen = ServerMatches) {
                ServerMatchesScreen(navController = navController)
            }

            composable(screen = MetricSets) {
                MetricSetListScreen(navController = navController)
            }

            navigation(
                initialScreen = MatchMetrics(),
                navigation = Metrics(),
            ) {
                composable(screen = MatchMetrics()) { backStackEntry ->
                    val metricSetId = backStackEntry.arguments?.getInt(Arguments.metricSetId.name) ?: 0
                    MetricsListScreen(
                        navController = navController,
                        category = MetricCategory.Match,
                        metricSetId = metricSetId
                    )
                }
                composable(screen = PitMetrics()) { backStackEntry ->
                    val metricSetId = backStackEntry.arguments?.getInt(Arguments.metricSetId.name) ?: 0
                    MetricsListScreen(
                        navController = navController,
                        category = MetricCategory.Pit,
                        metricSetId = metricSetId
                    )
                }
            }
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

// Wrapper function for adding a composable element using the Screen class
@ExperimentalAnimationApi
private fun NavGraphBuilder.navigation(
    initialScreen: Screen,
    navigation: Screen,
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
    builder: NavGraphBuilder.() -> Unit
) = navigation(
    startDestination = initialScreen.route,
    route = navigation.route,
    arguments = navigation.arguments,
    enterTransition = enterTransition,
    exitTransition = exitTransition,
    popEnterTransition = popEnterTransition,
    popExitTransition = popExitTransition,
    builder = builder,
)