package com.team2052.frckrawler.ui

import androidx.annotation.StringRes
import com.team2052.frckrawler.R

sealed class NavScreen(
    val route: String,
    @StringRes val titleResourceId: Int = R.string.app_name,
    val allowBackwardNavigation: Boolean = false,
) {

    object SplashScreen : NavScreen("splash_screen")

    object ModeSelectScreen : NavScreen("mode_select_screen")

    object ServerScreen : NavScreen("server_screen/{event}") {
        const val event: String = "event"
    }

    object ScoutScreen : NavScreen("scout_screen")

}