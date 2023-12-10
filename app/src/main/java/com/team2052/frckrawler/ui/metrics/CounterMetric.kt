package com.team2052.frckrawler.ui.metrics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.team2052.frckrawler.data.local.MetricCategory
import com.team2052.frckrawler.data.model.Metric
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme

@Composable
fun CounterMetric(
    state: String,
    onStateChanged: (String) -> Unit
) {
    Switch(
        checked = state.toBoolean(),
        onCheckedChange = { onStateChanged(it.toString()) }
    )
}

@Preview
@Composable
private fun CounterMetricPreview() {
    FrcKrawlerTheme {
        MetricInput(
            modifier = Modifier.fillMaxWidth().background(MaterialTheme.colors.surface),
            metric = Metric.CounterMetric(
                name = "Boolean metric",
                category = MetricCategory.Match,
                enabled = true,
                priority = 0,
                range = 1..10,
                step = 2
            ),
            state = "true",
            onStateChanged = {}
        )
    }
}