package com.team2052.frckrawler.ui.components._new

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Dp
import com.team2052.frckrawler.ui.theme.spaceLarge

@Composable
fun InputBlock(
    modifier: Modifier = Modifier,
    inputMargin: Dp = spaceLarge,
    content: @Composable () -> Unit,
) {
    var maxHeight by remember { mutableStateOf(0) }

    Layout(
        modifier = modifier,
        content = content,
    ) { measurables, constraints ->
        layout(constraints.maxWidth, maxHeight) {
            if (measurables.isNotEmpty()) {
                // Reset the height to avoid over expanding the block
                maxHeight = 0

                val individualInputWidth: Int = ((constraints.maxWidth - ((measurables.size - 1) * inputMargin.value)) / measurables.size).toInt()

                val individualInputConstraints = constraints.copy(
                    minWidth = individualInputWidth,
                    maxWidth = individualInputWidth,
                )

                measurables.forEachIndexed { index, measurable ->
                    val placeable = measurable.measure(individualInputConstraints)

                    maxHeight = maxOf(placeable.height, maxHeight)

                    placeable.placeRelative(
                        x = (individualInputWidth + inputMargin.value.toInt()) * index,
                        y = 0,
                    )
                }
            }
        }
    }
}