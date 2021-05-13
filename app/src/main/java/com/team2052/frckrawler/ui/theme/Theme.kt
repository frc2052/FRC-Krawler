package com.team2052.frckrawler.ui.theme

import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import com.team2052.frckrawler.compose.theme.*

/*
primary,
primaryVariant,
secondary,
secondaryVariant,
background,
surface,
error,
onPrimary,
onSecondary,
onBackground,
onSurface,
onError,
*/

private val DarkColorPalette = darkColors(
    primary =           FRCKrawlerColor.maroon200,
    primaryVariant =    FRCKrawlerColor.maroon500,
    onPrimary =         Color.White,

    secondary =         FRCKrawlerColor.yellow200,
    secondaryVariant =  FRCKrawlerColor.yellow200,
    onSecondary =       Color.Black,

    surface =           FRCKrawlerColor.backgroundDark,
    onSurface =         Color.White,

    background =        FRCKrawlerColor.backgroundDark,
    onBackground =      Color.White,

    error =             FRCKrawlerColor.red,
    onError =           Color.Black,
)

private val LightColorPalette = lightColors(
    primary =           FRCKrawlerColor.maroon200,
    primaryVariant =    FRCKrawlerColor.maroon500,
    onPrimary =         Color.White,

    secondary =         FRCKrawlerColor.yellow500,
    secondaryVariant =  FRCKrawlerColor.yellow200,
    onSecondary =       Color.Black,

    surface =           FRCKrawlerColor.backgroundLight,
    onSurface =         Color.Black,

    background =        FRCKrawlerColor.backgroundLight,
    onBackground =      Color.Black,

    error =             FRCKrawlerColor.red,
    onError =           Color.Black,
)

@Composable
fun FrcKrawlerTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(
        colors = if (darkTheme) DarkColorPalette else LightColorPalette,
        shapes = shapes,
        content = content
    )
}