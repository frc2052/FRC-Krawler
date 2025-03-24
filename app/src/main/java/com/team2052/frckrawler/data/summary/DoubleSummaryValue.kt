package com.team2052.frckrawler.data.summary

import java.text.NumberFormat

/**
 * Summary value that is simply a double value.
 */
data class DoubleSummaryValue(
  val value: Double,
  val isPercent: Boolean,
) : SummaryValue {
  companion object {
    private val PERCENT_FORMAT = NumberFormat.getPercentInstance()
    private val REGULAR_FORMAT = NumberFormat.getInstance()
  }
  override fun asDisplayString(): String = if (isPercent) {
    PERCENT_FORMAT.format(value / 100)
  } else {
    REGULAR_FORMAT.format(value)
  }
}