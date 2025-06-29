package com.team2052.frckrawler.data.model

import com.team2052.frckrawler.data.local.MetricType

sealed class Metric {
  abstract val id: String
  abstract val name: String
  abstract val priority: Int
  abstract val enabled: Boolean

  abstract fun defaultValue(): String

  data class BooleanMetric(
    override val id: String = "",
    override val name: String,
    override val priority: Int,
    override val enabled: Boolean,
  ) : Metric() {
    override fun defaultValue() = "false"
  }

  data class CounterMetric(
    override val id: String = "",
    override val name: String,
    override val priority: Int,
    override val enabled: Boolean,
    val range: IntProgression
  ) : Metric() {
    override fun defaultValue() = range.first.toString()
  }

  data class SliderMetric(
    override val id: String = "",
    override val name: String,
    override val priority: Int,
    override val enabled: Boolean,
    val range: IntProgression,
  ) : Metric() {
    override fun defaultValue() = range.first.toString()
  }

  data class ChooserMetric(
    override val id: String = "",
    override val name: String,
    override val priority: Int,
    override val enabled: Boolean,
    val options: List<String>,
  ) : Metric() {
    override fun defaultValue() = options.first()
  }

  data class CheckboxMetric(
    override val id: String = "",
    override val name: String,
    override val priority: Int,
    override val enabled: Boolean,
    val options: List<String>,
  ) : Metric() {
    override fun defaultValue() = ""
  }

  data class StopwatchMetric(
    override val id: String = "",
    override val name: String,
    override val priority: Int,
    override val enabled: Boolean,
  ) : Metric() {
    override fun defaultValue() = "0"
  }

  data class TextFieldMetric(
    override val id: String = "",
    override val name: String,
    override val priority: Int,
    override val enabled: Boolean,
  ) : Metric() {
    override fun defaultValue() = ""
  }

  data class SectionHeader(
    override val id: String = "",
    override val name: String,
    override val priority: Int,
    override val enabled: Boolean,
  ) : Metric() {
    override fun defaultValue(): String = ""
  }

  fun getType(): MetricType = when (this) {
    is BooleanMetric -> MetricType.Boolean
    is CheckboxMetric -> MetricType.Checkbox
    is ChooserMetric -> MetricType.Chooser
    is CounterMetric -> MetricType.Counter
    is SliderMetric -> MetricType.Slider
    is StopwatchMetric -> MetricType.Stopwatch
    is TextFieldMetric -> MetricType.TextField
    is SectionHeader -> MetricType.SectionHeader
  }
}