package com.team2052.frckrawler.data.summary

/**
 * Summary value that is a map of options to double values representing
 * a percentage
 */
data class OptionPercentageSummaryValue(
  val values: Map<String, Double>,
) : SummaryValue {
  override fun asDisplayString(): String = values.map { (option, percentage) ->
    "$option - $percentage%"
  }.joinToString(separator = "\n")
}