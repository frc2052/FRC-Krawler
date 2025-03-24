package com.team2052.frckrawler.data.summary

/**
 * Represents a value summary for a metric with no data
 */
object EmptySummaryValue : SummaryValue {
  override fun asDisplayString(): String = ""
}