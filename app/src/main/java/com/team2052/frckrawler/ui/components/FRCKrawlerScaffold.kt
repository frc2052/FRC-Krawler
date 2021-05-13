package com.team2052.frckrawler.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.team2052.frckrawler.R
import com.team2052.frckrawler.ui.NavScreen
import com.team2052.frckrawler.ui.theme.FRCKrawlerColor
import com.team2052.frckrawler.util.*

val LocalCardElevation = compositionLocalOf { 1.dp }

@Composable
fun FRCKrawlerScaffold(
    modifier: Modifier = Modifier,
    currentScreen: NavScreen,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    paddingValues: PaddingValues = PaddingValues(24.dp),
    onBackButtonPressed: () -> Unit = { },
    appBar: @Composable () -> Unit = @Composable {
        val allowBackwardNavigation = currentScreen.allowBackwardNavigation
        FRCKrawlerAppbar(
            backwardsNavigation = allowBackwardNavigation,
            onNavigationPressed = {
                if (allowBackwardNavigation) onBackButtonPressed()
            },
            title = {
                CompositionLocalProvider(LocalContentColor provides Color.White) {
                    Text(stringResource(currentScreen.titleResourceId).camelcase())
                }
            }
        )
    },
    background: @Composable () -> Unit = @Composable {
        Image(
            modifier = Modifier
                .fillMaxSize()
                .padding(48.dp),
            painter = painterResource(id = R.drawable.ic_bg_logo),
            contentDescription = "",
            colorFilter = ColorFilter.tint(
                if(isSystemInDarkTheme()) FRCKrawlerColor.logoTintDark else FRCKrawlerColor.logoTintLight
            ),
        )
    },
    content: @Composable ColumnScope.(Modifier) -> Unit,
) = Scaffold(
    modifier = modifier.fillMaxSize(),
    scaffoldState = scaffoldState,
    topBar = appBar,
) {
    CompositionLocalProvider(LocalCardElevation provides if (isSystemInDarkTheme()) 1.dp else 4.dp) {
        background()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            content = { content(Modifier.padding(bottom = 24.dp)) },
        )
    }
}