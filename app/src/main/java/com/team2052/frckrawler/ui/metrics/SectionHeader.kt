package com.team2052.frckrawler.ui.metrics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.team2052.frckrawler.data.model.Metric
import com.team2052.frckrawler.ui.FrcKrawlerPreview
import com.team2052.frckrawler.ui.common.StepControl
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme

@Composable
fun SectionHeader(
  modifier: Modifier = Modifier,
  metric: Metric,
) {
  Box(
    modifier = modifier
      .background(color = MaterialTheme.colorScheme.surfaceContainerLow)
      .padding(horizontal = 16.dp, vertical = 16.dp),
    contentAlignment = Alignment.CenterStart,
  ) {
    Text(
      text = metric.name,
      style = MaterialTheme.typography.headlineMedium,
      fontWeight = FontWeight.Bold,
    )
  }
}

@FrcKrawlerPreview
@Composable
private fun SectionHeaderPreview() {
  FrcKrawlerTheme {
    MetricInput(
      modifier = Modifier
        .fillMaxWidth()
        .background(MaterialTheme.colorScheme.surface),
      metric = Metric.SectionHeader(
        name = "Counter metric",
        enabled = true,
        priority = 0,
      ),
      state = "",
      onStateChanged = {}
    )
  }
}