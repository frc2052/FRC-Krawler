package com.team2052.frckrawler.data.local.transformer

import com.team2052.frckrawler.data.local.MetricRecord
import com.team2052.frckrawler.data.local.MetricType
import com.team2052.frckrawler.data.model.Metric

fun MetricRecord.toMetric(): Metric {
  return when (type) {
    MetricType.Boolean -> Metric.BooleanMetric(
      id = id,
      name = name,
      priority = priority,
      enabled = enabled
    )

    MetricType.Counter -> {
      val (min, max, step) = deserializeIntList()
      Metric.CounterMetric(
        id = id,
        name = name,
        priority = priority,
        enabled = enabled,
        range = min..max step step,
      )
    }

    MetricType.Slider -> {
      val (min, max, step) = deserializeIntList()
      Metric.SliderMetric(
        id = id,
        name = name,
        priority = priority,
        enabled = enabled,
        range = min..max step step
      )
    }

    MetricType.Chooser -> Metric.ChooserMetric(
      id = id,
      name = name,
      priority = priority,
      enabled = enabled,
      options = deserializeStringOptionsList()

    )

    MetricType.Checkbox -> Metric.CheckboxMetric(
      id = id,
      name = name,
      priority = priority,
      enabled = enabled,
      options = deserializeStringOptionsList()
    )

    MetricType.Stopwatch -> Metric.StopwatchMetric(
      id = id,
      name = name,
      priority = priority,
      enabled = enabled
    )

    MetricType.TextField -> Metric.TextFieldMetric(
      id = id,
      name = name,
      priority = priority,
      enabled = enabled
    )

    MetricType.SectionHeader -> Metric.SectionHeader(
      id = id,
      name = name,
      priority = priority,
      enabled = enabled
    )
  }
}

fun Metric.toMetricRecord(metricSetId: Int): MetricRecord {
  return when (this) {
    is Metric.BooleanMetric -> MetricRecord(
      id = id,
      metricSetId = metricSetId,
      type = MetricType.Boolean,
      name = name,
      priority = priority,
      enabled = enabled,
      options = null
    )

    is Metric.CheckboxMetric -> MetricRecord(
      id = id,
      metricSetId = metricSetId,
      type = MetricType.Checkbox,
      name = name,
      priority = priority,
      enabled = enabled,
      options = options.serializeToOptionsString()
    )

    is Metric.ChooserMetric -> MetricRecord(
      id = id,
      metricSetId = metricSetId,
      type = MetricType.Chooser,
      name = name,
      priority = priority,
      enabled = enabled,
      options = options.serializeToOptionsString()
    )

    is Metric.CounterMetric -> MetricRecord(
      id = id,
      metricSetId = metricSetId,
      type = MetricType.Counter,
      name = name,
      priority = priority,
      enabled = enabled,
      options = "${range.first},${range.last},${range.step}"
    )

    is Metric.SliderMetric -> MetricRecord(
      id = id,
      metricSetId = metricSetId,
      type = MetricType.Slider,
      name = name,
      priority = priority,
      enabled = enabled,
      options = "${range.first},${range.last},${range.step}"
    )

    is Metric.StopwatchMetric -> MetricRecord(
      id = id,
      metricSetId = metricSetId,
      type = MetricType.Stopwatch,
      name = name,
      priority = priority,
      enabled = enabled,
      options = null
    )

    is Metric.TextFieldMetric -> MetricRecord(
      id = id,
      metricSetId = metricSetId,
      type = MetricType.TextField,
      name = name,
      priority = priority,
      enabled = enabled,
      options = null
    )

    is Metric.SectionHeader -> MetricRecord(
      id = id,
      metricSetId = metricSetId,
      type = MetricType.SectionHeader,
      name = name,
      priority = priority,
      enabled = enabled,
      options = null
    )
  }
}

private fun MetricRecord.deserializeStringOptionsList(): List<String> {
  return options?.split(",") ?: emptyList()
}

private fun List<String>.serializeToOptionsString(): String {
  return joinToString(",")
}

private fun MetricRecord.deserializeIntList(): List<Int> {
  return options?.split(",")
    ?.mapNotNull { it.toIntOrNull() }
    ?: emptyList()
}