package com.team2052.frckrawler.ui.scout

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.team2052.frckrawler.R
import com.team2052.frckrawler.ui.FRCKrawlerAppState
import com.team2052.frckrawler.ui.Section
import timber.log.Timber

sealed class ScoutSections {
    object Scout : Section("scout", R.string.scout_screen_title)
    object ScoutMatch : Section("scout_match", R.string.scout_screen_title)
    object ScoutPit : Section("scout_pit", R.string.scout_screen_title)
}

fun NavGraphBuilder.scoutGraph(appState: FRCKrawlerAppState, modifier: Modifier) {
    navigation(
        route = ScoutSections.Scout.route,
        startDestination = ScoutSections.ScoutMatch.route,
    ) {

        composable(route = ScoutSections.ScoutMatch.route) {
            appState.title = R.string.scout_screen_title
            appState.topBarTabs = listOf(ScoutSections.ScoutMatch, ScoutSections.ScoutMatch)

            ScoutMatchScreen(modifier = modifier)
        }

        composable(route = ScoutSections.ScoutPit.route) {

        }
    }
}