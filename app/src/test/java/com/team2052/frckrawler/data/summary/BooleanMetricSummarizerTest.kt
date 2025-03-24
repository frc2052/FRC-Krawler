package com.team2052.frckrawler.data.summary

import com.team2052.frckrawler.data.export.generateMetricDatum
import com.team2052.frckrawler.data.local.MetricDatum
import com.team2052.frckrawler.data.model.Metric
import org.junit.Assert.assertEquals
import org.junit.Test

class BooleanMetricSummarizerTest {
  private val metric = Metric.BooleanMetric(
    id = "0",
    name = "Test",
    priority = 1,
    enabled = true,
  )

  @Test
  fun `summarize with all true values`() {
    val data = listOf(
      generateMetricDatum("true"),
      generateMetricDatum("true"),
      generateMetricDatum("true")
    )
    val result = BooleanMetricSummarizer.summarize(metric, data)
    assertEquals(DoubleSummaryValue(100.0, true), result)
  }

  @Test
  fun `summarize with all false values`() {
    val data = listOf(
      generateMetricDatum("false"),
      generateMetricDatum("false"),
      generateMetricDatum("false")
    )
    val result = BooleanMetricSummarizer.summarize(metric, data)
    assertEquals(DoubleSummaryValue(0.0, true), result)
  }

  @Test
  fun `summarize with mixed true and false values`() {
    val data = listOf(
      generateMetricDatum("true"),
      generateMetricDatum("false"),
      generateMetricDatum("true"),
      generateMetricDatum("true"),
    )
    val result = BooleanMetricSummarizer.summarize(metric, data)
    assertEquals(DoubleSummaryValue(75.0, true), result)
  }

  @Test
  fun `test summarize with no boolean values`() {
    val data = listOf(
      generateMetricDatum("yes"),
      generateMetricDatum("no"),
      generateMetricDatum("maybe")
    )
    val result = BooleanMetricSummarizer.summarize(metric, data)
    assertEquals(EmptySummaryValue, result)
  }

  @Test
  fun `test summarize with empty data`() {
    val data = emptyList<MetricDatum>()
    val result = BooleanMetricSummarizer.summarize(metric, data)
    assertEquals(EmptySummaryValue, result)
  }
}