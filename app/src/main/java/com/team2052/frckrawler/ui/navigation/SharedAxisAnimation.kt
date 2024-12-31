package com.team2052.frckrawler.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private const val DEFAULT_DURATION_MILLIS = 300
val DFEAULT_SLIDE_DISTANCE = 30.dp

fun sharedAxisEnterX(
  forward: Boolean,
  slideDistance: Int,
  durationMillis: Int = DEFAULT_DURATION_MILLIS,
): EnterTransition = slideInHorizontally(
  animationSpec = tween(
    durationMillis = durationMillis,
    easing = FastOutSlowInEasing
  ),
  initialOffsetX = {
    if (forward) slideDistance else -slideDistance
  },
) + fadeIn(
  animationSpec = tween(
    durationMillis = durationMillis,
    easing = FastOutSlowInEasing
  )
)

fun sharedAxisExitX(
  forward: Boolean,
  slideDistance: Int,
  durationMillis: Int = DEFAULT_DURATION_MILLIS,
): ExitTransition = slideOutHorizontally(
  animationSpec = tween(
    durationMillis = durationMillis,
    easing = FastOutSlowInEasing
  ),
  targetOffsetX = {
    if (forward) -slideDistance else slideDistance
  },
) + fadeOut(
  animationSpec = tween(
    durationMillis = durationMillis,
    easing = FastOutSlowInEasing
  )
)