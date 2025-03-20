package com.team2052.frckrawler.data.export.summary

import com.team2052.frckrawler.data.export.generateMetricDatum
import com.team2052.frckrawler.data.local.MetricDatum
import org.junit.Assert.assertEquals
import org.junit.Test

class FullStringMetricSummarizerTest {

  @Test
  fun `summarize single item`() {
    val data = listOf(
      generateMetricDatum("Didn't move", groupNumber = 1)
    )
    val result = FullStringMetricSummarizer.summarize(data)
    val expected = "\"1: Didn't move\""
    assertEquals(expected, result)
  }

  @Test
  fun `summarize with normal expected inputs`() {
    val data = listOf(
      generateMetricDatum("Didn't move", groupNumber = 1),
      generateMetricDatum("Got a red card", groupNumber = 27)
    )
    val result = FullStringMetricSummarizer.summarize(data)
    val expected = "\"1: Didn't move\n27: Got a red card\""
    assertEquals(expected, result)
  }

  @Test
  fun `summarize with empty values`() {
    val data = listOf(
      generateMetricDatum(value = "", groupNumber = 1),
      generateMetricDatum(value = "", groupNumber = 27)
    )
    val result = FullStringMetricSummarizer.summarize(data)
    val expected = "\"\""
    assertEquals(expected, result)
  }

  @Test
  fun `summarize with mixed empty and non-empty values`() {
    val data = listOf(
      generateMetricDatum(value = "Didn't move", groupNumber = 1),
      generateMetricDatum(value = "", groupNumber = 3),
      generateMetricDatum(value = "Got a red card", groupNumber = 27)
    )
    val result = FullStringMetricSummarizer.summarize(data)
    val expected = "\"1: Didn't move\n27: Got a red card\""
    assertEquals(expected, result)
  }

  @Test
  fun `summarize with empty data`() {
    val data = emptyList<MetricDatum>()
    val result = FullStringMetricSummarizer.summarize(data)
    val expected = "\"\""
    assertEquals(expected, result)
  }
}