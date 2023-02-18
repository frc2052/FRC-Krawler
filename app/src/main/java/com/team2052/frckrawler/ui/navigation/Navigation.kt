package com.team2052.frckrawler.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.*
import com.google.accompanist.navigation.animation.*
import com.team2052.frckrawler.ui.modeSelect.ModeSelectScreen
import com.team2052.frckrawler.ui.scout.ScoutHomeScreen
import com.team2052.frckrawler.ui.scout.ScoutMatchesScreen
import com.team2052.frckrawler.ui.navigation.Screen.*
import com.team2052.frckrawler.ui.server.ServerGamesScreen
import com.team2052.frckrawler.ui.server.home.ServerHomeScreen
import com.team2052.frckrawler.ui.server.ServerMatchesScreen
import com.team2052.frckrawler.ui.server.metrics.MatchMetricsScreen
import com.team2052.frckrawler.ui.server.metrics.PitMetricsScreen

private const val transitionOffset = 400
private const val transitionDuration = 400

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Navigation(initialScreen: Screen = ModeSelect) {
    // The universal navigation controller used for all navigation throughout the app.
    val navController = rememberAnimatedNavController()

    AnimatedNavHost(
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

            composable(screen = ServerGames) {
                ServerGamesScreen(navController = navController)
            }

            navigation(
                initialScreen = MatchMetrics,
                navigation = Metrics,
            ) {
                composable(screen = MatchMetrics) {
                    MatchMetricsScreen(navController = navController)
                }

                composable(screen = PitMetrics) {
                    PitMetricsScreen(navController = navController)
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
        AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition?
    )? = null,
    exitTransition: (
        AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition?
    )? = null,
    popEnterTransition: (
        AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition?
    )? = enterTransition,
    popExitTransition: (
        AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition?
    )? = exitTransition,
    content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit,
) = composable(
    route = screen.route,
    deepLinks = deepLinks,
    enterTransition = enterTransition,
    exitTransition = exitTransition,
    popEnterTransition = popEnterTransition,
    popExitTransition = popExitTransition,
    content = content
)

// Wrapper function for adding a composable element using the Screen class
@ExperimentalAnimationApi
private fun NavGraphBuilder.navigation(
    initialScreen: Screen,
    navigation: Screen,
    enterTransition: (
    AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition?
    )? = null,
    exitTransition: (
    AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition?
    )? = null,
    popEnterTransition: (
    AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition?
    )? = enterTransition,
    popExitTransition: (
    AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition?
    )? = exitTransition,
    builder: NavGraphBuilder.() -> Unit
) = navigation(
    startDestination = initialScreen.route,
    route = navigation.route,
    enterTransition = enterTransition,
    exitTransition = exitTransition,
    popEnterTransition = popEnterTransition,
    popExitTransition = popExitTransition,
    builder = builder,
)