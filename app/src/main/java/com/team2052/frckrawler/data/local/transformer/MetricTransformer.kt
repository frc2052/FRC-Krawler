package com.team2052.frckrawler.data.local.transformer

import com.team2052.frckrawler.data.local.MetricRecord
import com.team2052.frckrawler.data.local.MetricType
import com.team2052.frckrawler.data.model.Metric

fun MetricRecord.toMetric(): Metric {
    return when (type) {
        MetricType.Boolean -> Metric.BooleanMetric(
            id = id,
            name = name,
            category = category,
            priority = priority,
            enabled = enabled
        )
        MetricType.Counter -> {
            val (min, max, step) = deserializeIntList()
            Metric.CounterMetric(
                id = id,
                name = name,
                category = category,
                priority = priority,
                enabled = enabled,
                range = min..max,
                step = step
            )
        }
        MetricType.Slider -> {
            val (min, max) = deserializeIntList()
            Metric.SliderMetric(
                id = id,
                name = name,
                category = category,
                priority = priority,
                enabled = enabled,
                range = min..max
            )
        }
        MetricType.Chooser -> Metric.ChooserMetric(
            id = id,
            name = name,
            category = category,
            priority = priority,
            enabled = enabled,
            options = deserializeStringOptionsList()

            )
        MetricType.Checkbox -> Metric.CheckboxMetric(
            id = id,
            name = name,
            category = category,
            priority = priority,
            enabled = enabled,
            options = deserializeStringOptionsList()
        )
        MetricType.Stopwatch -> Metric.StopwatchMetric(
            id = id,
            name = name,
            category = category,
            priority = priority,
            enabled = enabled
        )
        MetricType.TextField -> Metric.TextFieldMetric(
            id = id,
            name = name,
            category = category,
            priority = priority,
            enabled = enabled
        )
    }
}

fun Metric.toMetricRecord(gameId: Int): MetricRecord {
    return when (this) {
        is Metric.BooleanMetric -> MetricRecord(
            id = id,
            gameId = gameId,
            type = MetricType.Boolean,
            name = name,
            category = category,
            priority = priority,
            enabled = enabled,
            options = null
        )
        is Metric.CheckboxMetric -> MetricRecord(
            id = id,
            gameId = gameId,
            type = MetricType.Checkbox,
            name = name,
            category = category,
            priority = priority,
            enabled = enabled,
            options = options.serializeToOptionsString()
        )
        is Metric.ChooserMetric -> MetricRecord(
            id = id,
            gameId = gameId,
            type = MetricType.Chooser,
            name = name,
            category = category,
            priority = priority,
            enabled = enabled,
            options = options.serializeToOptionsString()
        )
        is Metric.CounterMetric -> MetricRecord(
            id = id,
            gameId = gameId,
            type = MetricType.Slider,
            name = name,
            category = category,
            priority = priority,
            enabled = enabled,
            options = "${range.first},${range.last},$step"
        )
        is Metric.SliderMetric -> MetricRecord(
            id = id,
            gameId = gameId,
            type = MetricType.Slider,
            name = name,
            category = category,
            priority = priority,
            enabled = enabled,
            options = "${range.first},${range.last}"
        )
        is Metric.StopwatchMetric -> MetricRecord(
            id = id,
            gameId = gameId,
            type = MetricType.Stopwatch,
            name = name,
            category = category,
            priority = priority,
            enabled = enabled,
            options = null
        )
        is Metric.TextFieldMetric -> MetricRecord(
            id = id,
            gameId = gameId,
            type = MetricType.TextField,
            name = name,
            category = category,
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