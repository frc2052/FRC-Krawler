package com.team2052.frckrawler.data.summary

import com.team2052.frckrawler.data.export.generateMetricDatum
import com.team2052.frckrawler.data.local.MetricDatum
import org.junit.Assert.assertEquals
import org.junit.Test

class StringOptionsMetricSummarizerTest {

  @Test
  fun `summarize with normal input`() {
    val data = listOf(
      generateMetricDatum("Option 1"),
      generateMetricDatum("Option 2"),
      generateMetricDatum("Option 1"),
      generateMetricDatum("Option 1"),
    )
    val result = StringOptionsMetricSummarizer.summarize(data)
    val expected = mapOf(
      "Option 1" to 75.0,
      "Option 2" to 25.0
    )
    assertEquals(OptionPercentageSummaryValue(expected), result)
  }

  @Test
  fun `summarize with empty values`() {
    val data = listOf(
      generateMetricDatum(""),
      generateMetricDatum("")
    )
    val result = StringOptionsMetricSummarizer.summarize(data)
    assertEquals(EmptySummaryValue, result)
  }

  @Test
  fun `summarize with mixed empty and valid data`() {
    val data = listOf(
      generateMetricDatum("Option 1"),
      generateMetricDatum(""),
      generateMetricDatum("Option 2")
    )
    val result = StringOptionsMetricSummarizer.summarize(data)
    val expected = mapOf(
      "Option 1" to 50.0,
      "Option 2" to 50.0
    )
    assertEquals(OptionPercentageSummaryValue(expected), result)
  }

  @Test
  fun `summarize with single value`() {
    val data = listOf(
      generateMetricDatum("Option 1"),
    )
    val result = StringOptionsMetricSummarizer.summarize(data)
    val expected = mapOf(
      "Option 1" to 100.0
    )
    assertEquals(OptionPercentageSummaryValue(expected), result)
  }

  @Test
  fun `summarize with empty data`() {
    val data = emptyList<MetricDatum>()
    val result = StringOptionsMetricSummarizer.summarize(data)
    assertEquals(EmptySummaryValue, result)
  }

}