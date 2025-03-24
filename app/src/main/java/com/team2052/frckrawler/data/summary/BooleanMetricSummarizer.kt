package com.team2052.frckrawler.data.summary

import com.team2052.frckrawler.data.local.MetricDatum
import com.team2052.frckrawler.data.model.Metric

/**
 * Summarizes boolean metrics as an average of "true" values
 */
object BooleanMetricSummarizer: MetricSummarizer {
  override fun summarize(metric: Metric, data: List<MetricDatum>): SummaryValue {
    val values = data.mapNotNull { it.value.toBooleanStrictOrNull() }
    if (values.isEmpty()) {
      return EmptySummaryValue
    }
    val trueCount = values.count { it }
    val percentage = (trueCount / values.size.toDouble() * 100)
    return DoubleSummaryValue(value = percentage, isPercent = true)
  }
}