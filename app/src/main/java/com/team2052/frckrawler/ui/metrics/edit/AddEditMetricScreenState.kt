package com.team2052.frckrawler.ui.metrics.edit

import com.team2052.frckrawler.data.local.MetricCategory
import com.team2052.frckrawler.data.local.MetricType
import com.team2052.frckrawler.data.model.Metric

data class AddEditMetricScreenState(
    val name: String = "",
    val type: MetricType = MetricType.Boolean,
    val priority: Int = 0,
    val enabled: Boolean = true,
    val options: MetricOptions = MetricOptions.None,
) {
    val saveEnabled = name.isNotBlank() && options.isValid

    val previewMetric: Metric = toMetric(-1, 0)

    fun toMetric(
        id: Int,
        priority: Int
    ): Metric {
        return when (type) {
            MetricType.Boolean -> Metric.BooleanMetric(
                id = id,
                name = name,
                priority = priority,
                enabled = true
            )
            MetricType.Counter -> {
                if (options !is MetricOptions.SteppedIntRange) throw IllegalStateException("Wrong options type")
                Metric.CounterMetric(
                    id = id,
                    name = name,
                    priority = priority,
                    enabled = true,
                    range = options.range step options.step,
                )
            }
            MetricType.Slider -> {
                if (options !is MetricOptions.IntRange) throw IllegalStateException("Wrong options type")
                Metric.SliderMetric(
                    id = id,
                    name = name,
                    priority = priority,
                    enabled = true,
                    range = options.range
                )
            }
            MetricType.Chooser -> {
                if (options !is MetricOptions.StringList) throw IllegalStateException("Wrong options type")
                Metric.ChooserMetric(
                    id = id,
                    name = name,
                    priority = priority,
                    enabled = true,
                    options = options.options
                )
            }
            MetricType.Checkbox -> {
                if (options !is MetricOptions.StringList) throw IllegalStateException("Wrong options type")
                Metric.CheckboxMetric(
                    id = id,
                    name = name,
                    priority = priority,
                    enabled = true,
                    options = options.options
                )
            }
            MetricType.Stopwatch -> Metric.StopwatchMetric(
                id = id,
                name = name,
                priority = priority,
                enabled = true
            )
            MetricType.TextField -> Metric.TextFieldMetric(
                id = id,
                name = name,
                priority = priority,
                enabled = true
            )
        }
    }
}