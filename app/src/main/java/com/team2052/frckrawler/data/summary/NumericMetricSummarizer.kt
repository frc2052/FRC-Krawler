package com.team2052.frckrawler.data.summary

import com.team2052.frckrawler.data.local.MetricDatum
import com.team2052.frckrawler.data.model.Metric

/**
 * Summarizes numeric metrics with a simple average
 */
object NumericMetricSummarizer: MetricSummarizer {
  override fun summarize(metric: Metric, data: List<MetricDatum>): SummaryValue {
    val values = data.mapNotNull { it.value.toDoubleOrNull() }
    if (values.isEmpty()) return EmptySummaryValue

    return DoubleSummaryValue(value = values.average(), isPercent = false)
  }
}