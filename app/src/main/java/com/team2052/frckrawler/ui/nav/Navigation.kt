package com.team2052.frckrawler.ui.nav

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.*
import androidx.navigation.compose.NamedNavArgument
import com.google.accompanist.navigation.animation.*
import com.team2052.frckrawler.ui.modeSelect.ModeSelectScreen
import com.team2052.frckrawler.ui.scout.ScoutHomeScreen
import com.team2052.frckrawler.ui.scout.ScoutMatchesScreen
import com.team2052.frckrawler.ui.nav.Screen.*
import com.team2052.frckrawler.ui.server.ServerGamesScreen
import com.team2052.frckrawler.ui.server.ServerHomeScreen
import com.team2052.frckrawler.ui.server.ServerMatchesScreen
import com.team2052.frckrawler.ui.server.metrics.MatchMetricsScreen
import com.team2052.frckrawler.ui.server.metrics.PitMetricsScreen

private const val transitionOffset = 400
private const val transitionDuration = 400

// Enter transition smoothly brings the screen in from the right.
@ExperimentalAnimationApi
private val defaultEnterTransition = slideInHorizontally(
    initialOffsetX = { transitionOffset },
    animationSpec = tween(transitionDuration)
) + fadeIn(animationSpec = tween(transitionDuration))

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Navigation(initialScreen: Screen = ModeSelect) {
    // The universal navigation controller used for all navigation throughout the app.
    val navController = rememberAnimatedNavController()
    AnimatedNavHost(
        navController = navController,
        startDestination = initialScreen.route,
        enterTransition = { _, _ ->
            EnterTransition.None
        },
        exitTransition = { _, _ ->
            ExitTransition.None
        },
    ) {
        composable(
            screen = ModeSelect,
            enterTransition = { initial, _ ->
                // Check if mode select is the first screen and run the startup animation accordingly.
                if (initial.destination.route == null) {
                    fadeIn(animationSpec = tween(transitionDuration))
                } else {
                    slideInHorizontally(
                        initialOffsetX = { -transitionOffset },
                        animationSpec = tween(transitionDuration)
                    ) + fadeIn(animationSpec = tween(transitionDuration))
                }
            },
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
                navigation = Metrics,
                initialScreen = MatchMetrics,
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

@ExperimentalAnimationApi
private fun NavGraphBuilder.composable(
    screen: Screen,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    enterTransition: (
        (initial: NavBackStackEntry, target: NavBackStackEntry) -> EnterTransition?
    )? = null,
    exitTransition: (
        (initial: NavBackStackEntry, target: NavBackStackEntry) -> ExitTransition?
    )? = null,
    popEnterTransition: (
        (initial: NavBackStackEntry, target: NavBackStackEntry) -> EnterTransition?
    )? = enterTransition,
    popExitTransition: (
        (initial: NavBackStackEntry, target: NavBackStackEntry) -> ExitTransition?
    )? = exitTransition,
    content: @Composable (NavBackStackEntry) -> Unit,
) = composable(
    route = screen.route,
    arguments = arguments,
    deepLinks = deepLinks,
    enterTransition = enterTransition,
    exitTransition = exitTransition,
    popEnterTransition = popEnterTransition,
    popExitTransition = popExitTransition,
    content = content
)

@ExperimentalAnimationApi
private fun NavGraphBuilder.navigation(
    navigation: Screen,
    initialScreen: Screen,
    enterTransition: (
        (initial: NavBackStackEntry, target: NavBackStackEntry) -> EnterTransition
    )? = null,
    exitTransition: (
        (initial: NavBackStackEntry, target: NavBackStackEntry) -> ExitTransition
    )? = null,
    popEnterTransition: (
        (initial: NavBackStackEntry, target: NavBackStackEntry) -> EnterTransition
    )? = enterTransition,
    popExitTransition: (
        (initial: NavBackStackEntry, target: NavBackStackEntry) -> ExitTransition
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