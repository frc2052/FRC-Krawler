package com.team2052.frckrawler.data.export.converter

import com.team2052.frckrawler.data.export.CsvSummaryDataRow
import com.team2052.frckrawler.data.export.generateMetric
import com.team2052.frckrawler.data.local.TeamAtEvent
import org.junit.Assert.assertEquals
import org.junit.Test

class SummaryMetricsCsvRowConverterTest {

  @Test
  fun `header without team names`() {
    val metrics = listOf(
      generateMetric("Metric 1"),
      generateMetric("Metric 2")
    )
    val converter = SummaryMetricsCsvRowConverter(includeTeamNames = false)
    val expectedHeader = "\"Team Number\",\"Metric 1\",\"Metric 2\"\n"
    assertEquals(expectedHeader, converter.getHeader(metrics))
  }

  @Test
  fun `header with team names`() {
    val metrics = listOf(
      generateMetric("Metric 1"),
      generateMetric("Metric 2")
    )
    val converter = SummaryMetricsCsvRowConverter(includeTeamNames = true)
    val expectedHeader = "\"Team Number\",\"Team Name\",\"Metric 1\",\"Metric 2\"\n"
    assertEquals(expectedHeader, converter.getHeader(metrics))
  }

  @Test
  fun `match group without team names`() {
    val csvDataRow = CsvSummaryDataRow(
      teamAtEvent = TeamAtEvent("2052", "KnightKrawler", 0),
      data = listOf("60", "Yes")
    )
    val converter = SummaryMetricsCsvRowConverter(includeTeamNames = false)
    val expectedRow = "\"2052\",\"60\",\"Yes\""
    assertEquals(expectedRow, converter.getDataRow(csvDataRow))
  }

  @Test
  fun `match group with team names`() {
    val csvDataRow = CsvSummaryDataRow(
      teamAtEvent = TeamAtEvent("2052", "KnightKrawler", 0),
      data = listOf("60", "Yes")
    )
    val converter = SummaryMetricsCsvRowConverter(includeTeamNames = true)
    val expectedRow = "\"2052\",\"KnightKrawler\",\"60\",\"Yes\""
    assertEquals(expectedRow, converter.getDataRow(csvDataRow))
  }

  @Test
  fun `pit group without team names`() {
    val csvDataRow = CsvSummaryDataRow(
      teamAtEvent = TeamAtEvent("2052", "KnightKrawler", 0),
      data = listOf("Blue")
    )
    val converter = SummaryMetricsCsvRowConverter(includeTeamNames = false)
    val expectedRow = "\"2052\",\"Blue\""
    assertEquals(expectedRow, converter.getDataRow(csvDataRow))
  }

  @Test
  fun `pit group with team names`() {
    val csvDataRow = CsvSummaryDataRow(
      teamAtEvent = TeamAtEvent("2052", "KnightKrawler", 0),
      data = listOf("Blue")
    )
    val converter = SummaryMetricsCsvRowConverter(includeTeamNames = true)
    val expectedRow = "\"2052\",\"KnightKrawler\",\"Blue\""
    assertEquals(expectedRow, converter.getDataRow(csvDataRow))
  }

  @Test
  fun `null data has empty value`() {
    val csvDataRow = CsvSummaryDataRow(
      teamAtEvent = TeamAtEvent("2052", "KnightKrawler", 0),
      data = listOf(null)
    )
    val converter = SummaryMetricsCsvRowConverter(includeTeamNames = true)
    val expectedRow = "\"2052\",\"KnightKrawler\","
    assertEquals(expectedRow, converter.getDataRow(csvDataRow))
  }
}