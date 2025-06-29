package com.team2052.frckrawler.data.summary

import com.team2052.frckrawler.data.local.MetricDatum
import com.team2052.frckrawler.data.model.Metric

/**
 * Always returns empty summary value
 */
object NoOpSummarizer: MetricSummarizer {
  override fun summarize(metric: Metric, data: List<MetricDatum>): SummaryValue {
    return EmptySummaryValue
  }
}