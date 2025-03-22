package com.team2052.frckrawler.ui.scout

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.team2052.frckrawler.data.model.MetricState
import com.team2052.frckrawler.ui.metrics.MetricInput

@Composable
fun ScoutingForm(
  header: @Composable () -> Unit,
  metrics: List<MetricState>,
  onMetricStateChanged: (MetricState) -> Unit,
  modifier: Modifier = Modifier,
  contentPadding: PaddingValues = PaddingValues(0.dp),
) {
  LazyColumn(
    modifier = modifier
      .imePadding()
      .consumeWindowInsets(contentPadding),
    contentPadding = contentPadding,
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

      HorizontalDivider()
    }

    item {
      // Leave some extra space for the FAB so it doesn't cover up controls
      Spacer(modifier.height(56.dp))
    }
  }
}
