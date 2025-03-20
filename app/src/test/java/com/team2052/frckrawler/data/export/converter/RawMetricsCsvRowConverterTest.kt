package com.team2052.frckrawler.data.export.converter

import com.team2052.frckrawler.data.export.CsvRawDataRow
import com.team2052.frckrawler.data.export.generateMetric
import com.team2052.frckrawler.data.export.generateMetricDatum
import com.team2052.frckrawler.data.local.MetricDatumGroup
import com.team2052.frckrawler.data.local.TeamAtEvent
import org.junit.Assert.assertEquals
import org.junit.Test

class RawMetricsCsvRowConverterTest {

  @Test
  fun `header without team names`() {
    val metrics = listOf(
      generateMetric("Metric 1"),
      generateMetric("Metric 2")
    )
    val converter = RawMetricsCsvRowConverter(includeTeamNames = false)
    val expectedHeader = "\"Team Number\",\"Type\",\"Match Number\",\"Metric 1\",\"Metric 2\"\n"
    assertEquals(expectedHeader, converter.getHeader(metrics))
  }

  @Test
  fun `header with team names`() {
    val metrics = listOf(
      generateMetric("Metric 1"),
      generateMetric("Metric 2")
    )
    val converter = RawMetricsCsvRowConverter(includeTeamNames = true)
    val expectedHeader = "\"Team Number\",\"Team Name\",\"Type\",\"Match Number\",\"Metric 1\",\"Metric 2\"\n"
    assertEquals(expectedHeader, converter.getHeader(metrics))
  }

  @Test
  fun `match group without team names`() {
    val csvDataRow = CsvRawDataRow(
      teamAtEvent = TeamAtEvent("2052", "KnightKrawler", 0),
      group = MetricDatumGroup.Match,
      groupNumber = 1,
      data = listOf(
        generateMetricDatum(value = "60", group = MetricDatumGroup.Match, groupNumber = 1),
        generateMetricDatum(value = "Yes", group = MetricDatumGroup.Match, groupNumber = 1)
      )
    )
    val converter = RawMetricsCsvRowConverter(includeTeamNames = false)
    val expectedRow = "\"2052\",\"Match\",\"1\",\"60\",\"Yes\"\n"
    assertEquals(expectedRow, converter.getDataRow(csvDataRow))
  }

  @Test
  fun `match group with team names`() {
    val csvDataRow = CsvRawDataRow(
      teamAtEvent = TeamAtEvent("2052", "KnightKrawler", 0),
      group = MetricDatumGroup.Match,
      groupNumber = 1,
      data = listOf(
        generateMetricDatum(value = "60", group = MetricDatumGroup.Match, groupNumber = 1),
        generateMetricDatum(value = "Yes", group = MetricDatumGroup.Match, groupNumber = 1)
      )
    )
    val converter = RawMetricsCsvRowConverter(includeTeamNames = true)
    val expectedRow = "\"2052\",\"KnightKrawler\",\"Match\",\"1\",\"60\",\"Yes\"\n"
    assertEquals(expectedRow, converter.getDataRow(csvDataRow))
  }

  @Test
  fun `pit group without team names`() {
    val csvDataRow = CsvRawDataRow(
      teamAtEvent = TeamAtEvent("2052", "KnightKrawler", 0),
      group = MetricDatumGroup.Pit,
      groupNumber = 0,
      data = listOf(
        generateMetricDatum(value = "Blue", group = MetricDatumGroup.Pit, groupNumber = 0)
      )
    )
    val converter = RawMetricsCsvRowConverter(includeTeamNames = false)
    val expectedRow = "\"2052\",\"Pit\",\"0\",\"Blue\"\n"
    assertEquals(expectedRow, converter.getDataRow(csvDataRow))
  }

  @Test
  fun `pit group with team names`() {
    val csvDataRow = CsvRawDataRow(
      teamAtEvent = TeamAtEvent("2052", "KnightKrawler", 0),
      group = MetricDatumGroup.Pit,
      groupNumber = 0,
      data = listOf(
        generateMetricDatum(value = "Blue", group = MetricDatumGroup.Pit, groupNumber = 0)
      )
    )
    val converter = RawMetricsCsvRowConverter(includeTeamNames = true)
    val expectedRow = "\"2052\",\"KnightKrawler\",\"Pit\",\"0\",\"Blue\"\n"
    assertEquals(expectedRow, converter.getDataRow(csvDataRow))
  }

  @Test
  fun `null data has empty value`() {
    val csvDataRow = CsvRawDataRow(
      teamAtEvent = TeamAtEvent("2052", "KnightKrawler", 0),
      group = MetricDatumGroup.Pit,
      groupNumber = 0,
      data = listOf(
        null
      )
    )
    val converter = RawMetricsCsvRowConverter(includeTeamNames = true)
    val expectedRow = "\"2052\",\"KnightKrawler\",\"Pit\",\"0\",\n"
    assertEquals(expectedRow, converter.getDataRow(csvDataRow))
  }
}