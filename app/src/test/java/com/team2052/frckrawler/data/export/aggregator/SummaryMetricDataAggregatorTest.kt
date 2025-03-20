package com.team2052.frckrawler.data.export.aggregator

import com.team2052.frckrawler.data.export.CsvSummaryDataRow
import com.team2052.frckrawler.data.export.generateMetric
import com.team2052.frckrawler.data.export.generateMetricDatum
import com.team2052.frckrawler.data.local.MetricDatum
import com.team2052.frckrawler.data.local.MetricType
import com.team2052.frckrawler.data.local.TeamAtEvent
import org.junit.Assert.assertEquals
import org.junit.Test

class SummaryMetricDataAggregatorTest {

  private val teamsByNumber = mapOf(
    "2052" to TeamAtEvent("2052", "KnightKrawler", 1),
    "1114" to TeamAtEvent("1114", "Simbotics", 1)
  )

  private val metrics = listOf(
    generateMetric(id = "1", name = "Metric 1", type = MetricType.Boolean),
    generateMetric(id = "2", name = "Metric 2", type = MetricType.Counter),
  )

  private val aggregator = SummaryMetricDataAggregator(teamsByNumber)

  @Test
  fun `aggregate with normal inputs`() {
    val data = listOf(
      generateMetricDatum(value = "true", metricId = "1", teamNumber = "2052"),
      generateMetricDatum(value = "5", metricId = "2", teamNumber = "2052"),
      generateMetricDatum(value = "false", metricId = "1", teamNumber = "1114"),
      generateMetricDatum(value = "10", metricId = "2", teamNumber = "1114"),
    )
    val result = aggregator.aggregate(metrics, data)
    val expected = listOf(
      CsvSummaryDataRow(teamsByNumber["2052"]!!, listOf("100.0", "5.0")),
      CsvSummaryDataRow(teamsByNumber["1114"]!!, listOf("0.0", "10.0"))
    )
    assertEquals(expected, result)
  }

  @Test
  fun `sorts correctly`() {
    val reversedMetrics = listOf(
      generateMetric(id = "2", name = "Metric 2", type = MetricType.Counter),
      generateMetric(id = "1", name = "Metric 1", type = MetricType.Boolean),
    )
    val data = listOf(
      generateMetricDatum(value = "true", metricId = "1", teamNumber = "2052"),
      generateMetricDatum(value = "5", metricId = "2", teamNumber = "2052"),
      generateMetricDatum(value = "false", metricId = "1", teamNumber = "1114"),
      generateMetricDatum(value = "10", metricId = "2", teamNumber = "1114"),
    )
    val result = aggregator.aggregate(reversedMetrics, data)
    val expected = listOf(
      CsvSummaryDataRow(teamsByNumber["2052"]!!, listOf("5.0", "100.0")),
      CsvSummaryDataRow(teamsByNumber["1114"]!!, listOf("10.0", "0.0"))
    )
    assertEquals(expected, result)
  }

  @Test
  fun `aggregate with empty data`() {
    val data = emptyList<MetricDatum>()
    val result = aggregator.aggregate(metrics, data)
    assertEquals(emptyList<CsvSummaryDataRow>(), result)
  }

  @Test
  fun `aggregate with unknown team`() {
    val data = listOf(
      generateMetricDatum(value = "true", metricId = "1", teamNumber = "9999"),
    )
    val result = aggregator.aggregate(metrics, data)
    val expected = listOf(
      CsvSummaryDataRow(TeamAtEvent("9999", "Unknown", 0), listOf("100.0", ""))
    )
    assertEquals(expected, result)
  }

  @Test
  fun `aggregate with unknown metric`() {
    val data = listOf(
      generateMetricDatum(value = "10", metricId = "3", teamNumber = "2052"),
    )
    val result = aggregator.aggregate(metrics, data)
    val expected = listOf(
      CsvSummaryDataRow(TeamAtEvent("2052", "KnightKrawler", 1), listOf("", ""))
    )
    assertEquals(expected, result)
  }

  @Test
  fun `aggregate with mixed valid and invalid data`() {
    val data = listOf(
      generateMetricDatum(value = "true", metricId = "1", teamNumber = "2052"),
      generateMetricDatum(value = "invalid", metricId = "2", teamNumber = "2052"),
      generateMetricDatum(value = "false", metricId = "1", teamNumber = "1114"),
      generateMetricDatum(value = "10", metricId = "2", teamNumber = "1114"),
    )
    val result = aggregator.aggregate(metrics, data)
    val expected = listOf(
      CsvSummaryDataRow(teamsByNumber["2052"]!!, listOf("100.0", "")),
      CsvSummaryDataRow(teamsByNumber["1114"]!!, listOf("0.0", "10.0"))
    )
    assertEquals(expected, result)
  }

  @Test
  fun `aggregate with empty metrics`() {
    val data = listOf(
      generateMetricDatum(value = "true", metricId = "1", teamNumber = "2052"),
      generateMetricDatum(value = "5", metricId = "2", teamNumber = "2052"),
    )
    val result = aggregator.aggregate(emptyList(), data)
    val expected = listOf(
      CsvSummaryDataRow(teamsByNumber["2052"]!!, emptyList()),
    )
    assertEquals(expected, result)
  }

  // Unknown metric
}