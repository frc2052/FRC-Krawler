package com.team2052.frckrawler.ui.metrics

import com.team2052.frckrawler.data.local.MetricCategory
import com.team2052.frckrawler.data.local.MetricType
import com.team2052.frckrawler.data.model.Metric

data class AddEditMetricScreenState(
    val name: String = "",
    val type: MetricType? = null,
    val priority: Int = 0,
    val enabled: Boolean = true,
    val options: TypeOptions = TypeOptions.None,
    val previewMetric: Metric? = null
) {
    val saveEnabled = name.isNotBlank() && type != null && options.isValid

    fun toMetric(
        id: Int,
        category: MetricCategory,
        priority: Int
    ): Metric? {
        return when (type) {
            MetricType.Boolean -> Metric.BooleanMetric(
                id = id,
                name = name,
                category = category,
                priority = priority,
                enabled = true
            )
            MetricType.Counter -> {
                if (options !is TypeOptions.SteppedIntRange) return null
                Metric.CounterMetric(
                    id = id,
                    name = name,
                    category = category,
                    priority = priority,
                    enabled = true,
                    range = options.range,
                    step = options.step
                )
            }
            MetricType.Slider -> {
                if (options !is TypeOptions.IntRange) return null
                Metric.SliderMetric(
                    id = id,
                    name = name,
                    category = category,
                    priority = priority,
                    enabled = true,
                    range = options.range
                )
            }
            MetricType.Chooser -> {
                if (options !is TypeOptions.StringList) return null
                Metric.ChooserMetric(
                    id = id,
                    name = name,
                    category = category,
                    priority = priority,
                    enabled = true,
                    options = options.options
                )
            }
            MetricType.Checkbox -> {
                if (options !is TypeOptions.StringList) return null
                Metric.CheckboxMetric(
                    id = id,
                    name = name,
                    category = category,
                    priority = priority,
                    enabled = true,
                    options = options.options
                )
            }
            MetricType.Stopwatch -> Metric.StopwatchMetric(
                id = id,
                name = name,
                category = category,
                priority = priority,
                enabled = true
            )
            MetricType.TextField -> Metric.TextFieldMetric(
                id = id,
                name = name,
                category = category,
                priority = priority,
                enabled = true
            )
            null -> null
        }
    }
}