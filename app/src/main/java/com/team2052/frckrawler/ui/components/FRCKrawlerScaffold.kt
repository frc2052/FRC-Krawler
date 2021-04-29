package com.team2052.frckrawler.ui.components

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

@Composable
fun FRCKrawlerScaffold(
    darkTheme: Boolean = isSystemInDarkTheme(),
    paddingValues: PaddingValues = PaddingValues(24.dp),
    content: @Composable () -> Unit
) = FrcKrawlerTheme(darkTheme = darkTheme) {
    Scaffold(
        topBar = { FRCKrawlerAppbar(title = {
            CompositionLocalProvider(
                LocalContentColor provides Color.White,
                LocalContentAlpha provides ContentAlpha.high,
                content = { Text(stringResource(id = R.string.app_name)) }
            )
        }) }
    ) {
        Image(
            modifier = Modifier.fillMaxSize().padding(48.dp),
            painter = painterResource(id = R.drawable.ic_bg_logo),
            contentDescription = "",
            colorFilter = ColorFilter.tint(if(darkTheme) FRCKrawlerColor.logoTintDark else FRCKrawlerColor.logoTintLight)
        )
        Column(modifier = Modifier.padding(paddingValues)) { content() }
    }
}