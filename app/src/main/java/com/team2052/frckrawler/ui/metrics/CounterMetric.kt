package com.team2052.frckrawler.ui.metrics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.team2052.frckrawler.data.model.Metric
import com.team2052.frckrawler.ui.FrcKrawlerPreview
import com.team2052.frckrawler.ui.common.StepControl
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme

@Composable
fun CounterMetric(
  range: IntProgression,
  state: Int,
  onStateChanged: (Int) -> Unit
) {
  StepControl(
    value = state,
    onValueChanged = onStateChanged,
    step = range.step,
    range = range.first..range.last
  )
}

@FrcKrawlerPreview
@Composable
private fun CounterMetricPreview() {
  FrcKrawlerTheme {
    MetricInput(
      modifier = Modifier
        .fillMaxWidth()
        .background(MaterialTheme.colorScheme.surface),
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