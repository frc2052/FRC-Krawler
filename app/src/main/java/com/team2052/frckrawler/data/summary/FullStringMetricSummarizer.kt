package com.team2052.frckrawler.data.summary

import com.team2052.frckrawler.data.local.MetricDatum

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
  override fun summarize(data: List<MetricDatum>): String {
    return data.filter { it.value.isNotEmpty() }
      .joinToString(
      separator = "\n",
      transform = { "${it.groupNumber}: ${it.value}" }
    )
  }
}