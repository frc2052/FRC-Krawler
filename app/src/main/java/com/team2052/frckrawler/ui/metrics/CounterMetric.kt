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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.team2052.frckrawler.data.model.Metric
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme

@Composable
fun CounterMetric(
    range: IntProgression,
    state: Int,
    onStateChanged: (Int) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { onStateChanged(state - range.step) },
            enabled = range.contains(state - range.step)
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = "Decrease value",
                tint = MaterialTheme.colors.primary
            )
        }

        Spacer(Modifier.width(8.dp))
        
        Text(
            text = state.toString(),
            style = MaterialTheme.typography.h5
        )

        Spacer(Modifier.width(8.dp))

        IconButton(
            onClick = { onStateChanged(state + range.step) },
            enabled = range.contains(state + range.step)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Increase value",
                tint = MaterialTheme.colors.primary
            )
        }
    }
}

@Preview
@Composable
private fun CounterMetricPreview() {
    FrcKrawlerTheme {
        MetricInput(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.surface),
            metric = Metric.CounterMetric(
                name = "Counter metric",
                enabled = true,
                priority = 0,
                range = 1..10 step 2
            ),
            state = "1",
            onStateChanged = {}
        )
    }
}