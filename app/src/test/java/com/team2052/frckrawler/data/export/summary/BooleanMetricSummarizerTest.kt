package com.team2052.frckrawler.data.export.summary

import com.team2052.frckrawler.data.export.generateMetricDatum
import com.team2052.frckrawler.data.local.MetricDatum
import org.junit.Assert.assertEquals
import org.junit.Test

class BooleanMetricSummarizerTest {

  @Test
  fun `summarize with all true values`() {
    val data = listOf(
      generateMetricDatum("true"),
      generateMetricDatum("true"),
      generateMetricDatum("true")
    )
    val result = BooleanMetricSummarizer.summarize(data)
    assertEquals("100.0", result)
  }

  @Test
  fun `summarize with all false values`() {
    val data = listOf(
      generateMetricDatum("false"),
      generateMetricDatum("false"),
      generateMetricDatum("false")
    )
    val result = BooleanMetricSummarizer.summarize(data)
    assertEquals("0.0", result)
  }

  @Test
  fun `summarize with mixed true and false values`() {
    val data = listOf(
      generateMetricDatum("true"),
      generateMetricDatum("false"),
      generateMetricDatum("true"),
      generateMetricDatum("true"),
    )
    val result = BooleanMetricSummarizer.summarize(data)
    assertEquals("75.0", result)
  }

  @Test
  fun `test summarize with no boolean values`() {
    val data = listOf(
      generateMetricDatum("yes"),
      generateMetricDatum("no"),
      generateMetricDatum("maybe")
    )
    val result = BooleanMetricSummarizer.summarize(data)
    assertEquals("", result)
  }

  @Test
  fun `test summarize with empty data`() {
    val data = emptyList<MetricDatum>()
    val result = BooleanMetricSummarizer.summarize(data)
    assertEquals("", result)
  }
}