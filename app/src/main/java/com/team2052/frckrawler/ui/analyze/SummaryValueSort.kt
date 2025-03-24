package com.team2052.frckrawler.ui.analyze

import com.team2052.frckrawler.data.summary.DoubleSummaryValue
import com.team2052.frckrawler.data.summary.EmptySummaryValue
import com.team2052.frckrawler.data.summary.OptionPercentageSummaryValue
import com.team2052.frckrawler.data.summary.RawStringListSummaryValue

/**
 * Map summary values to a numeric value useful for sorting
 *
 * @param mode The sort mode to use, only used to ensure empty values are ordered last (use standard
 *  language sort functionality to configure ascending/descending)
 * @param option and optional string value to use to sort data from multiple choice metrics
 */
fun TeamMetricData.sortValue(mode: AnalyzeSortMode, option: String?): Double {
  return when (this.summary) {
    // Empty values should always be last, adjust for sort mode
    // Fun fact - Double.MIN_VALUE is positive, so we want negative Double.MAX_VALUE
    is EmptySummaryValue -> if (mode == AnalyzeSortMode.Ascending) Double.MAX_VALUE else -Double.MAX_VALUE

    // Find the correct option for the sort and use that value
    is OptionPercentageSummaryValue -> {
      val optionValue = this.summary.values[option]
      optionValue ?: 0.0
    }

    // Doubles are easy, use the value
    is DoubleSummaryValue -> this.summary.value

    // No defined sort for raw strings
    is RawStringListSummaryValue -> 0.0
    else -> 0.0
  }
}