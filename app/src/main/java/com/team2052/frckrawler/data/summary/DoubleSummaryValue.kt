package com.team2052.frckrawler.data.summary

/**
 * Summary value that is simply a double value.
 */
data class DoubleSummaryValue(
  val value: Double,
  val isPercent: Boolean,
) : SummaryValue {
  override fun asDisplayString(): String = if (isPercent) {
    "$value%"
  } else {
    value.toString()
  }
}