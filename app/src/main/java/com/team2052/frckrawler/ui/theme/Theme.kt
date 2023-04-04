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
    primary =           yellow200,
    primaryVariant =    yellow500,
    onPrimary =         black,

    secondary =         maroon200,
    secondaryVariant =  maroon500,
    onSecondary =       white,

    surface =           black,
    onSurface =         Color(0xFFFFFFFF),

    background =        Color(0xFF121212),
    onBackground =      Color(0xFFFFFFFF),

    error =             red,
    onError =           white,
)

private val LightColorPalette = lightColors(
    primary =           maroon200,
    primaryVariant =    maroon500,
    onPrimary =         white,

    secondary =         yellow200,
    secondaryVariant =  yellow500,
    onSecondary =       black,

    surface =           white,
    onSurface =         black,

    background =        white,
    onBackground =      black,

    error =             red,
    onError =           black,
)

@Composable
fun FrcKrawlerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) = MaterialTheme(
    colors = if (darkTheme) DarkColorPalette else LightColorPalette,
    shapes = shapes,
    content = content
)