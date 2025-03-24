package com.team2052.frckrawler.data.summary

import java.text.NumberFormat

/**
 * Summary value that is a map of options to double values representing
 * a percentage
 */
data class OptionPercentageSummaryValue(
  val values: Map<String, Double>,
) : SummaryValue {
  companion object {
    private val PERCENT_FORMAT = NumberFormat.getPercentInstance()
  }

  override fun asDisplayString(): String = values.map { (option, percentage) ->
    val formattedPercent = PERCENT_FORMAT.format(percentage / 100)
    "$option - $formattedPercent"
  }.joinToString(separator = "\n")
}