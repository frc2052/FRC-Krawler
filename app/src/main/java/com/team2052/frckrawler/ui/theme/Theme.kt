package com.team2052.frckrawler.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
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
    primary =           maroon200,
    primaryVariant =    maroon500,
    onPrimary =         white,

    secondary =         yellow200,
    secondaryVariant =  yellow500,
    onSecondary =       black,

    surface =           black,
    onSurface =         white,

    background =        black,
    onBackground =      white,

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

// TODO create fully custom color set
val Colors.secondarySurface: Color
    get() = if (isLight) goldSurface else darkMaroonSurface

@Composable
fun FrcKrawlerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) = MaterialTheme(
    colors = if (darkTheme) DarkColorPalette else LightColorPalette,
    shapes = shapes,
    content = content
)