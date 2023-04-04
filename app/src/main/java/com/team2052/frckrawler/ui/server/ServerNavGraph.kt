package com.team2052.frckrawler.ui.server

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.team2052.frckrawler.R
import com.team2052.frckrawler.ui.FRCKrawlerAppState
import com.team2052.frckrawler.ui.Section

sealed class ServerSections {
    object Server : Section("server", R.string.server_screen_title)
    object ServerHome : Section("server_home", R.string.server_screen_title)
}

fun NavGraphBuilder.serverGraph(appState: FRCKrawlerAppState, modifier: Modifier) {
    navigation(
        route = ServerSections.Server.route,
        startDestination = ServerSections.ServerHome.route,
    ) {
        appState.topBarTabs = listOf(ServerSections.ServerHome)

        composable(route = ServerSections.ServerHome.route) { backStackEntry ->
            ServerHomeScreen(
                modifier = modifier,
            )
        }
    }
}