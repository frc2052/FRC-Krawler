package com.team2052.frckrawler.data.summary

import com.team2052.frckrawler.data.local.MetricDatum
import com.team2052.frckrawler.data.model.Metric

/**
 * Summarizes a metric by including the full text of each piece of data.
 * This will include the "group number" (typically match number) to label each item.
 *
 * Example output:
 * ```
 *   1: Didn't move
 *   26: Got a red card
 * ```
 */
object FullStringMetricSummarizer: MetricSummarizer {
  override fun summarize(metric: Metric, data: List<MetricDatum>): SummaryValue {
    val stringValues =  data.filter { it.value.isNotEmpty() }
      .map { "${it.groupNumber}: ${it.value}" }
    if (stringValues.isEmpty()) return EmptySummaryValue
    return RawStringListSummaryValue(stringValues)
  }
}