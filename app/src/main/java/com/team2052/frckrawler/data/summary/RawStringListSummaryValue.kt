package com.team2052.frckrawler.data.summary

/**
 * Summary value that is a list of strings that cannot be represented
 * numerically.
 */
data class RawStringListSummaryValue(
  val values: List<String>,
) : SummaryValue {
  override fun asDisplayString(): String = values.joinToString(
    separator = "\n",
  )
}