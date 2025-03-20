package com.team2052.frckrawler.data.export.summary

import com.team2052.frckrawler.data.local.MetricDatum

/**
 * Summarizes boolean metrics as an average of "true" values
 */
object BooleanMetricSummarizer: MetricSummarizer {
  override fun summarize(data: List<MetricDatum>): String {
    val values = data.mapNotNull { it.value.toBooleanStrictOrNull() }
    if (values.isEmpty()) {
      return ""
    }
    val trueCount = values.count { it }
    return (trueCount / values.size.toDouble() * 100).toString()
  }
}