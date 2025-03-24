package com.team2052.frckrawler.data.summary

import com.team2052.frckrawler.data.local.MetricDatum

/**
 * Summarizes numeric metrics with a simple average
 */
object NumericMetricSummarizer: MetricSummarizer {
  override fun summarize(data: List<MetricDatum>): String {
    val values = data.mapNotNull { it.value.toDoubleOrNull() }
    if (values.isEmpty()) return ""

    return values.average().toString()
  }
}