package com.team2052.frckrawler.ui.metrics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.team2052.frckrawler.data.local.MetricCategory
import com.team2052.frckrawler.data.model.Metric
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun StopwatchMetric(
    state: String,
    onStateChanged: (String) -> Unit
) {
    var isRunning by remember { mutableStateOf(false) }
    var elapsedTime by remember { mutableDoubleStateOf(state.toDoubleOrNull() ?: 0.0) }
    LaunchedEffect(isRunning) {
        if (isRunning) {
            while (true) {
                delay(100.milliseconds)
                elapsedTime += .1
            }
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { elapsedTime = 0.0 }) {
            Icon(
                imageVector = Icons.Default.RestartAlt,
                tint = MaterialTheme.colors.primary,
                contentDescription = "play"
            )
        }

        Spacer(Modifier.width(4.dp))

        Text(
            text = String.format("%.1f", elapsedTime),
            style = MaterialTheme.typography.h6
        )

        Spacer(Modifier.width(4.dp))

        IconButton(onClick = {
            isRunning = !isRunning
            if (!isRunning) {
                onStateChanged(elapsedTime.toString())
            }
        }) {
            if (isRunning) {
                Icon(
                    imageVector = Icons.Default.Pause,
                    tint = MaterialTheme.colors.primary,
                    contentDescription = "pause"
                )
            } else {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    tint = MaterialTheme.colors.primary,
                    contentDescription = "play"
                )
            }
        }
    }
}

@Preview
@Composable
private fun StopwatchMetricPreview() {
    FrcKrawlerTheme {
        MetricInput(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.surface),
            metric = Metric.StopwatchMetric(
                name = "Stopwatch metric",
                category = MetricCategory.Match,
                enabled = true,
                priority = 0
            ),
            state = "",
            onStateChanged = {}
        )
    }
}