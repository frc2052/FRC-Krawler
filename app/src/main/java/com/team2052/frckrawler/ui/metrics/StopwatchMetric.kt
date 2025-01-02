package com.team2052.frckrawler.ui.metrics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.team2052.frckrawler.R
import com.team2052.frckrawler.data.model.Metric
import com.team2052.frckrawler.ui.FrcKrawlerPreview
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun StopwatchMetric(
  state: String,
  onStateChanged: (String) -> Unit
) {
  var isRunning by remember { mutableStateOf(false) }
  var elapsedTime by remember(state) { mutableDoubleStateOf(state.toDoubleOrNull() ?: 0.0) }
  LaunchedEffect(isRunning) {
    if (isRunning) {
      while (true) {
        delay(100.milliseconds)
        elapsedTime += .1
      }
    }
  }

  Row(
    verticalAlignment = Alignment.CenterVertically
  ) {
    IconButton(onClick = { elapsedTime = 0.0 }) {
      Icon(
        imageVector = Icons.Default.RestartAlt,
        tint = MaterialTheme.colorScheme.primary,
        contentDescription = stringResource(R.string.metric_stopwatch_reset)
      )
    }

    Spacer(Modifier.width(4.dp))

    Text(
      text = String.format("%.1f", elapsedTime),
      style = MaterialTheme.typography.titleLarge
    )

    Spacer(Modifier.width(4.dp))

    IconButton(onClick = {
      isRunning = !isRunning
      if (!isRunning) {
        onStateChanged(elapsedTime.toString())
      }
    }) {
      if (isRunning) {
        Icon(
          imageVector = Icons.Default.Pause,
          tint = MaterialTheme.colorScheme.primary,
          contentDescription = stringResource(R.string.metric_stopwatch_pause)
        )
      } else {
        Icon(
          imageVector = Icons.Default.PlayArrow,
          tint = MaterialTheme.colorScheme.primary,
          contentDescription = stringResource(R.string.metric_stopwatch_start)
        )
      }
    }
  }
}

@FrcKrawlerPreview
@Composable
private fun StopwatchMetricPreview() {
  FrcKrawlerTheme {
    Surface {
      MetricInput(
        modifier = Modifier
          .fillMaxWidth()
          .background(MaterialTheme.colorScheme.surface),
        metric = Metric.StopwatchMetric(
          name = "Stopwatch metric",
          enabled = true,
          priority = 0
        ),
        state = "",
        onStateChanged = {}
      )
    }
  }
}