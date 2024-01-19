package com.team2052.frckrawler.ui.metrics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.team2052.frckrawler.data.model.Metric
import com.team2052.frckrawler.ui.FrcKrawlerPreview
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme

@Composable
fun CheckboxMetric(
  state: String,
  onStateChanged: (String) -> Unit,
  options: List<String>
) {
  val checkedOptions = remember(state) { state.split(",").toSet() }
  Column {
    options.forEach { option ->
      Row(
        verticalAlignment = Alignment.CenterVertically
      ) {
        Checkbox(
          checked = checkedOptions.contains(option),
          onCheckedChange = { checked ->
            val newCheckedOptions = if (checked) {
              checkedOptions + option
            } else {
              checkedOptions - option
            }
            onStateChanged(newCheckedOptions.joinToString(","))
          }
        )

        Text(
          text = option
        )
      }
    }
  }
}

@FrcKrawlerPreview
@Composable
private fun CheckboxMetricPreview() {
  var state by remember { mutableStateOf("two") }
  FrcKrawlerTheme {
    MetricInput(
      modifier = Modifier
        .fillMaxWidth()
        .background(MaterialTheme.colors.surface),
      metric = Metric.CheckboxMetric(
        name = "Checkbox metric",
        enabled = true,
        priority = 0,
        options = listOf("one", "two", "three")
      ),
      state = state,
      onStateChanged = { state = it }
    )
  }
}