package com.team2052.frckrawler.ui.metrics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import com.team2052.frckrawler.data.model.Metric
import com.team2052.frckrawler.ui.FrcKrawlerPreview
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme

@Composable
fun TextFieldMetric(
  state: String,
  onStateChanged: (String) -> Unit
) {
  TextField(
    modifier = Modifier.fillMaxWidth(),
    value = state,
    onValueChange = onStateChanged,
    keyboardOptions = KeyboardOptions(
      capitalization = KeyboardCapitalization.Sentences
    )
  )
}

@FrcKrawlerPreview
@Composable
private fun TextFieldMetricPreview() {
  FrcKrawlerTheme {
    MetricInput(
      modifier = Modifier
        .fillMaxWidth()
        .background(MaterialTheme.colorScheme.surface),
      metric = Metric.TextFieldMetric(
        name = "TextField metric",
        enabled = true,
        priority = 0
      ),
      state = "Sample content",
      onStateChanged = {}
    )
  }
}