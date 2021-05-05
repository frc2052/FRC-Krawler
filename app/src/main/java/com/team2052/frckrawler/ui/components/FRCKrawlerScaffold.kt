package com.team2052.frckrawler.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.team2052.frckrawler.R
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme
import com.team2052.frckrawler.ui.theme.FRCKrawlerColor
import com.team2052.frckrawler.util.*

val LocalCardElevation = compositionLocalOf { 1.dp }

@Composable
fun FRCKrawlerScaffold(
    modifier: Modifier = Modifier,
    @StringRes titleResourceId: Int,
    paddingValues: PaddingValues = PaddingValues(24.dp),
    content: @Composable () -> Unit
) = FrcKrawlerTheme {
    Scaffold(
        modifier = modifier,
        topBar = {
            FRCKrawlerAppbar(title = {
                CompositionLocalProvider(LocalContentColor provides Color.White) {
                    Text(stringResource(id = titleResourceId).camelcase())
                }
            })
        }
    ) {
        CompositionLocalProvider(LocalCardElevation provides if (isSystemInDarkTheme()) 1.dp else 4.dp) {
            Image(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(48.dp),
                painter = painterResource(id = R.drawable.ic_bg_logo),
                contentDescription = "",
                colorFilter = ColorFilter.tint(if(isSystemInDarkTheme()) FRCKrawlerColor.logoTintDark else FRCKrawlerColor.logoTintLight)
            )
            Column(modifier = Modifier.padding(paddingValues)) { content() }
        }
    }
}