package com.team2052.frckrawler.data.summary

import com.team2052.frckrawler.data.local.MetricDatum
import com.team2052.frckrawler.data.model.Metric

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
  override fun summarize(metric: Metric, data: List<MetricDatum>): SummaryValue {
    val values = data.map { it.value }
      .filter { it.isNotEmpty() }
      .map { it.split(",") }
      .flatten()
    if (values.isEmpty()) {
      return EmptySummaryValue
    }

    val total = values.size.toDouble()
    val dataByOption = values.groupBy { it }
    val dataPercentages = dataByOption.mapValues { (_, data) ->
      data.size / total * 100
    }

    val allOptions: List<String> = when (metric) {
      is Metric.ChooserMetric -> metric.options
      is Metric.CheckboxMetric -> metric.options
      else -> emptyList()
    }

    val optionPercentages = allOptions.associate { option ->
      Pair(option, dataPercentages[option] ?: 0.0)
    }

    return OptionPercentageSummaryValue(optionPercentages)
  }
}