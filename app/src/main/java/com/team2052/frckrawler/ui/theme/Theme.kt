package com.team2052.frckrawler.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

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
    secondaryVariant =  FRCKrawlerColor.yellow500,
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

    secondary =         FRCKrawlerColor.yellow200,
    secondaryVariant =  FRCKrawlerColor.yellow500,
    onSecondary =       Color.Black,

    surface =           Color(0xFFFFFFFF),
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