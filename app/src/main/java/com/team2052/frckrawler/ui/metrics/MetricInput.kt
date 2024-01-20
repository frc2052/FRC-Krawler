package com.team2052.frckrawler.ui.metrics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.team2052.frckrawler.data.model.Metric

@Composable
fun MetricInput(
  modifier: Modifier = Modifier,
  metric: Metric,
  state: String,
  onStateChanged: (String) -> Unit
) {
  when (metric) {
    is Metric.BooleanMetric -> {
      MetricRow(
        modifier = modifier,
        metric = metric
      ) {
        BooleanMetric(state = state, onStateChanged = onStateChanged)
      }
    }

    is Metric.CounterMetric -> {
      MetricRow(
        modifier = modifier,
        metric = metric
      ) {
        CounterMetric(
          state = state.toInt(),
          onStateChanged = { onStateChanged(it.toString()) },
          range = metric.range,
        )
      }
    }

    is Metric.SliderMetric -> {
      MetricRow(
        modifier = modifier,
        metric = metric
      ) {
        SliderMetric(
          state = state.toInt(),
          onStateChanged = { onStateChanged(it.toString()) },
          range = metric.range
        )
      }
    }

    is Metric.CheckboxMetric -> {
      MetricRow(
        modifier = modifier,
        metric = metric
      ) {
        CheckboxMetric(
          state = state,
          onStateChanged = onStateChanged,
          options = metric.options
        )
      }
    }

    is Metric.ChooserMetric -> {
      MetricRow(
        modifier = modifier,
        metric = metric
      ) {
        ChooserMetric(
          state = state,
          onStateChanged = onStateChanged,
          options = metric.options
        )
      }
    }

    is Metric.StopwatchMetric -> {
      MetricRow(
        modifier = modifier,
        metric = metric
      ) {
        StopwatchMetric(
          state = state,
          onStateChanged = onStateChanged
        )
      }
    }

    is Metric.TextFieldMetric -> {
      MetricColumn(
        modifier = modifier,
        metric = metric
      ) {
        TextFieldMetric(
          state = state,
          onStateChanged = onStateChanged
        )
      }
    }
  }
}

@Composable
private fun MetricRow(
  modifier: Modifier = Modifier,
  metric: Metric,
  content: @Composable () -> Unit
) {
  Row(
    modifier = modifier
      .background(color = MaterialTheme.colors.surface)
      .padding(horizontal = 16.dp, vertical = 16.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(
      text = metric.name,
      style = MaterialTheme.typography.h5
    )

    Spacer(Modifier.width(16.dp))

    content()
  }
}

@Composable
private fun MetricColumn(
  modifier: Modifier = Modifier,
  metric: Metric,
  content: @Composable () -> Unit
) {
  Column(
    modifier = modifier
      .background(color = MaterialTheme.colors.surface)
      .padding(horizontal = 16.dp, vertical = 16.dp)
  ) {
    Text(
      text = metric.name,
      style = MaterialTheme.typography.h5
    )

    Spacer(Modifier.height(8.dp))

    content()
  }
}