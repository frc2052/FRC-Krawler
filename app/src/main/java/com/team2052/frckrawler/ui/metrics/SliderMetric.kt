package com.team2052.frckrawler.ui.metrics

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.team2052.frckrawler.data.local.MetricCategory
import com.team2052.frckrawler.data.model.Metric
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme
import kotlin.math.roundToInt

@Composable
fun SliderMetric(
    range: IntProgression,
    state: Int,
    onStateChanged: (Int) -> Unit
) {
    var sliderPosition by remember { mutableFloatStateOf(state.toFloat()) }
    val interactionSource = remember { MutableInteractionSource() }
    val isDragging by interactionSource.collectIsDraggedAsState()

    Column {
        Slider(
            interactionSource = interactionSource,
            value = if (isDragging) sliderPosition else state.toFloat(),
            valueRange = range.first.toFloat()..range.last.toFloat(),
            steps = range.count() - 2,
            onValueChange = {
                sliderPosition = it
                onStateChanged(sliderPosition.roundToInt())
            },
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = range.first.toString()
            )
            Text(
                text = state.toString(),
                style = MaterialTheme.typography.h6
            )
            Text(
                text = range.last.toString()
            )
        }

    }
}

@Preview
@Composable
private fun SliderMetricPreview() {
    var state by remember { mutableStateOf("3") }
    FrcKrawlerTheme {
        MetricInput(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.surface),
            metric = Metric.SliderMetric(
                name = "Slider metric",
                category = MetricCategory.Match,
                enabled = true,
                priority = 0,
                range = 1..10
            ),
            state = state,
            onStateChanged = { state = it }
        )
    }
}