package com.team2052.frckrawler.compose.theme

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
    primary = maroon200,
    primaryVariant = maroon500,
    onPrimary = Color.White,
    secondary = yellow200,
    onSecondary = Color.White,
    surface = black121,
)

private val LightColorPalette = lightColors(
    primary = maroon200,
    primaryVariant = maroon500,
    onPrimary = Color.White,
    secondary = yellow200,
    onSecondary = Color.Black,
    surface = black121
)

@Composable
fun FrcKrawlerTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(
        colors = if (darkTheme) DarkColorPalette else LightColorPalette,
        shapes = shapes,
        content = content
    )
}