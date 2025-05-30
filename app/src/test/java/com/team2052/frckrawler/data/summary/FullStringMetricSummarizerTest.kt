package com.team2052.frckrawler.data.summary

import com.team2052.frckrawler.data.export.generateMetricDatum
import com.team2052.frckrawler.data.local.MetricDatum
import com.team2052.frckrawler.data.model.Metric
import org.junit.Assert.assertEquals
import org.junit.Test

class FullStringMetricSummarizerTest {
  private val metric = Metric.TextFieldMetric(
    id = "0",
    name = "TextField",
    priority = 1,
    enabled = true,
  )

  @Test
  fun `summarize single item`() {
    val data = listOf(
      generateMetricDatum("Didn't move", groupNumber = 1)
    )
    val result = FullStringMetricSummarizer.summarize(metric, data)
    val expected = listOf("1: Didn't move")
    assertEquals(RawStringListSummaryValue(expected), result)
  }

  @Test
  fun `summarize with normal expected inputs`() {
    val data = listOf(
      generateMetricDatum("Didn't move", groupNumber = 1),
      generateMetricDatum("Got a red card", groupNumber = 27)
    )
    val result = FullStringMetricSummarizer.summarize(metric, data)
    val expected = listOf("1: Didn't move", "27: Got a red card")
    assertEquals(RawStringListSummaryValue(expected), result)
  }

  @Test
  fun `summarize with empty values`() {
    val data = listOf(
      generateMetricDatum(value = "", groupNumber = 1),
      generateMetricDatum(value = "", groupNumber = 27)
    )
    val result = FullStringMetricSummarizer.summarize(metric, data)
    assertEquals(EmptySummaryValue, result)
  }

  @Test
  fun `summarize with mixed empty and non-empty values`() {
    val data = listOf(
      generateMetricDatum(value = "Didn't move", groupNumber = 1),
      generateMetricDatum(value = "", groupNumber = 3),
      generateMetricDatum(value = "Got a red card", groupNumber = 27)
    )
    val result = FullStringMetricSummarizer.summarize(metric, data)
    val expected = listOf("1: Didn't move", "27: Got a red card")
    assertEquals(RawStringListSummaryValue(expected), result)
  }

  @Test
  fun `summarize with empty data`() {
    val data = emptyList<MetricDatum>()
    val result = FullStringMetricSummarizer.summarize(metric, data)
    assertEquals(EmptySummaryValue, result)
  }
}