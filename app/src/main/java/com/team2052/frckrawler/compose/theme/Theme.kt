package com.team2052.frckrawler.compose.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = maroon200,
    primaryVariant = maroon500,
    onPrimary = Color.White,
    secondary = yellow200,
    onSecondary = Color.Black
)

private val LightColorPalette = lightColors(
    primary = maroon200,
    primaryVariant = maroon500,
    onPrimary = Color.White,
    secondary = yellow200,
    onSecondary = Color.Black
)

@Composable
fun FrcKrawlerTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable() () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        shapes = shapes,
        content = content
    )
}
