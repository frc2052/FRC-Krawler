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
    val expected = "Option 1 - 75.0%\nOption 2 - 25.0%"
    assertEquals(expected, result)
  }

  @Test
  fun `summarize with empty values`() {
    val data = listOf(
      generateMetricDatum(""),
      generateMetricDatum("")
    )
    val result = StringOptionsMetricSummarizer.summarize(data)
    val expected = ""
    assertEquals(expected, result)
  }

  @Test
  fun `summarize with mixed empty and valid data`() {
    val data = listOf(
      generateMetricDatum("Option 1"),
      generateMetricDatum(""),
      generateMetricDatum("Option 2")
    )
    val result = StringOptionsMetricSummarizer.summarize(data)
    val expected = "Option 1 - 50.0%\nOption 2 - 50.0%"
    assertEquals(expected, result)
  }

  @Test
  fun `summarize with single value`() {
    val data = listOf(
      generateMetricDatum("Option 1"),
    )
    val result = StringOptionsMetricSummarizer.summarize(data)
    val expected = "Option 1 - 100.0%"
    assertEquals(expected, result)
  }

  @Test
  fun `summarize with empty data`() {
    val data = emptyList<MetricDatum>()
    val result = StringOptionsMetricSummarizer.summarize(data)
    val expected = ""
    assertEquals(expected, result)
  }

}