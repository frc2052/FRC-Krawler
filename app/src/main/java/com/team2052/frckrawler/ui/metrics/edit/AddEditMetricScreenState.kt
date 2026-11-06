package com.team2052.frckrawler.ui.metrics.edit

import androidx.compose.foundation.text.input.TextFieldState
import com.team2052.frckrawler.data.local.MetricType
import com.team2052.frckrawler.data.model.Metric

data class AddEditMetricScreenState(
  val name: TextFieldState = TextFieldState(),
  val type: MetricType = MetricType.Boolean,
  val priority: Int = -1,
  val enabled: Boolean = true,
  val options: MetricOptions = MetricOptions.None,
) {
  val saveEnabled: Boolean
    get() = name.text.isNotBlank() && options.isValid

  val previewMetric: Metric
    get() = toMetric("no-id", 0)

  fun toMetric(
    id: String,
    priority: Int
  ): Metric {
    val metricName = name.text.toString()
    return when (type) {
      MetricType.Boolean -> Metric.BooleanMetric(
        id = id,
        name = metricName,
        priority = priority,
        enabled = enabled
      )

      MetricType.Counter -> {
        if (options !is MetricOptions.SteppedIntRange) throw IllegalStateException("Wrong options type")
        Metric.CounterMetric(
          id = id,
          name = metricName,
          priority = priority,
          enabled = enabled,
          range = options.range step options.step,
        )
      }

      MetricType.Slider -> {
        if (options !is MetricOptions.IntRange) throw IllegalStateException("Wrong options type")
        Metric.SliderMetric(
          id = id,
          name = metricName,
          priority = priority,
          enabled = enabled,
          range = options.range
        )
      }

      MetricType.Chooser -> {
        if (options !is MetricOptions.StringList) throw IllegalStateException("Wrong options type")
        Metric.ChooserMetric(
          id = id,
          name = metricName,
          priority = priority,
          enabled = enabled,
          options = options.options
        )
      }

      MetricType.Checkbox -> {
        if (options !is MetricOptions.StringList) throw IllegalStateException("Wrong options type")
        Metric.CheckboxMetric(
          id = id,
          name = metricName,
          priority = priority,
          enabled = enabled,
          options = options.options
        )
      }

      MetricType.Stopwatch -> Metric.StopwatchMetric(
        id = id,
        name = metricName,
        priority = priority,
        enabled = enabled
      )

      MetricType.TextField -> Metric.TextFieldMetric(
        id = id,
        name = metricName,
        priority = priority,
        enabled = enabled
      )

      MetricType.SectionHeader -> Metric.SectionHeader(
        id = id,
        name = metricName,
        priority = priority,
        enabled = enabled
      )
    }
  }
}
