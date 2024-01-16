package com.team2052.frckrawler.ui.scout.match

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.team2052.frckrawler.ui.metrics.MetricInput

@Composable
fun ScoutingForm(
    header: @Composable () -> Unit,
    metrics: List<MetricState>,
    onMetricStateChanged: (MetricState) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
    ) {
        item {
            header()
        }

        items(metrics) { metric ->
            MetricInput(
                modifier = Modifier.fillMaxWidth(),
                metric = metric.metric,
                state = metric.value,
                onStateChanged = { newValue ->
                    onMetricStateChanged(
                        metric.copy(value = newValue)
                    )
                }
            )

            if (metric != metrics.last()) {
                Divider()
            }
        }
    }
}
