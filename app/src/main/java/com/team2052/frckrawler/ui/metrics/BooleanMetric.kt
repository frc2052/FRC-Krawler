package com.team2052.frckrawler.ui.metrics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.team2052.frckrawler.data.local.MetricRecord
import com.team2052.frckrawler.data.local.MetricCategory
import com.team2052.frckrawler.data.local.MetricType
import com.team2052.frckrawler.data.model.Metric
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme

@Composable
fun BooleanMetric(
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
private fun BooleanMetricPreview() {
    FrcKrawlerTheme {
        MetricInput(
            modifier = Modifier.fillMaxWidth().background(MaterialTheme.colors.surface),
            metric = Metric.BooleanMetric(
                name = "Boolean metric",
                category = MetricCategory.Match,
                enabled = true,
                priority = 0
            ),
            state = "true",
            onStateChanged = {}
        )
    }
}