package com.team2052.frckrawler.ui.metrics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.team2052.frckrawler.data.local.MetricCategory
import com.team2052.frckrawler.data.model.Metric
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme

@Composable
fun TextFieldMetric(
    state: String,
    onStateChanged: (String) -> Unit
) {
    TextField(
        modifier =  Modifier.fillMaxWidth(),
        value = state,
        onValueChange = onStateChanged
    )
}

@Preview
@Composable
private fun TextFieldMetricPreview() {
    FrcKrawlerTheme {
        MetricInput(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.surface),
            metric = Metric.TextFieldMetric(
                name = "TextField metric",
                category = MetricCategory.Match,
                enabled = true,
                priority = 0
            ),
            state = "Sample content",
            onStateChanged = {}
        )
    }
}