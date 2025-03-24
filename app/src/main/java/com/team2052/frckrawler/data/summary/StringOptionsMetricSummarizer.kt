package com.team2052.frckrawler.data.summary

import com.team2052.frckrawler.data.local.MetricDatum

/**
 * Summarizes a metric that has a finite set of String-based options
 * E.g. Chooser, Checkbox
 *
 * The output will be a list of options with a percentage
 * for how often each option was selected
 *
 * Example output:
 * ```
 *   Option 1 - 50.0%
 *   Option 2 - 25.0%
 *   Option 3 - 25.0%
 * ```
 */
object StringOptionsMetricSummarizer: MetricSummarizer {
  override fun summarize(data: List<MetricDatum>): String {
    val values = data.map { it.value }
      .filter { it.isNotEmpty() }
      .map { it.split(",") }
      .flatten()
    if (values.isEmpty()) {
      return ""
    }

    val total = values.size.toDouble()
    val dataByOption = values.groupBy { it }
    val percentageByOption = dataByOption.mapValues { (_, data) ->
      data.size / total * 100
    }
    return percentageByOption.map { (option, percentage) ->
      "$option - $percentage%"
    }.joinToString(separator = "\n")
  }
}