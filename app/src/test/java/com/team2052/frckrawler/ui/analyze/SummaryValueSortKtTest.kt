package com.team2052.frckrawler.ui.analyze

import com.team2052.frckrawler.data.local.TeamAtEvent
import com.team2052.frckrawler.data.summary.DoubleSummaryValue
import com.team2052.frckrawler.data.summary.EmptySummaryValue
import com.team2052.frckrawler.data.summary.OptionPercentageSummaryValue
import com.team2052.frckrawler.data.summary.RawStringListSummaryValue
import junit.framework.Assert.assertEquals
import org.junit.Test

class SummaryValueSortKtTest {

  private val team1 = generateTeam(1)
  private val team2 = generateTeam(2)
  private val team3 = generateTeam(3)

  @Test
  fun `empty value is last when ascending`() {
    val data = listOf(
      TeamMetricData(team1, EmptySummaryValue),
      TeamMetricData(team2, DoubleSummaryValue(1.0, isPercent = false)),
      TeamMetricData(team3, DoubleSummaryValue(2.0, isPercent = false)),
    )
    val sorted = data.sortedBy { it.sortValue(AnalyzeSortMode.Ascending, null) }

    val expected = listOf(
      TeamMetricData(team2, DoubleSummaryValue(1.0, isPercent = false)),
      TeamMetricData(team3, DoubleSummaryValue(2.0, isPercent = false)),
      TeamMetricData(team1, EmptySummaryValue),
    )
    assertEquals(expected, sorted)
  }

  @Test
  fun `empty value is last when descending`() {
    val data = listOf(
      TeamMetricData(team1, EmptySummaryValue),
      TeamMetricData(team2, DoubleSummaryValue(1.0, isPercent = false)),
      TeamMetricData(team3, DoubleSummaryValue(2.0, isPercent = false)),
    )

    val sorted = data.sortedByDescending { it.sortValue(AnalyzeSortMode.Descending, null) }

    val expected = listOf(
      TeamMetricData(team3, DoubleSummaryValue(2.0, isPercent = false)),
      TeamMetricData(team2, DoubleSummaryValue(1.0, isPercent = false)),
      TeamMetricData(team1, EmptySummaryValue),
    )
    assertEquals(expected, sorted)
  }

  @Test
  fun `empty value desc below 0`() {
    val data = listOf(
      TeamMetricData(team1, EmptySummaryValue),
      TeamMetricData(team2, DoubleSummaryValue(0.0, isPercent = false)),
      TeamMetricData(team3, DoubleSummaryValue(2.0, isPercent = false)),
    )
    val mapped = data.map { it.sortValue(AnalyzeSortMode.Descending, null) }
    System.err.println(mapped)
    System.err.println(mapped.sorted())
    val sorted = data.sortedByDescending { it.sortValue(AnalyzeSortMode.Descending, null) }

    val expected = listOf(
      TeamMetricData(team3, DoubleSummaryValue(2.0, isPercent = false)),
      TeamMetricData(team2, DoubleSummaryValue(0.0, isPercent = false)),
      TeamMetricData(team1, EmptySummaryValue),
    )
    assertEquals(expected, sorted)
  }

  @Test
  fun `double value sort`() {
    val data = listOf(
      TeamMetricData(team2, DoubleSummaryValue(2.0, isPercent = false)),
      TeamMetricData(team3, DoubleSummaryValue(3.0, isPercent = false)),
      TeamMetricData(team1, DoubleSummaryValue(1.0, isPercent = false)),
    )
    val sorted = data.sortedBy { it.sortValue(AnalyzeSortMode.Ascending, null) }

    val expected = listOf(
      TeamMetricData(team1, DoubleSummaryValue(1.0, isPercent = false)),
      TeamMetricData(team2, DoubleSummaryValue(2.0, isPercent = false)),
      TeamMetricData(team3, DoubleSummaryValue(3.0, isPercent = false)),
    )
    assertEquals(expected, sorted)
  }

  @Test
  fun `option value sort`() {
    val data1 = TeamMetricData(team1, OptionPercentageSummaryValue(
        mapOf(
          "Option 1" to 10.0,
          "Option 2" to 90.0
        )
      ))
    val data2 = TeamMetricData(team2, OptionPercentageSummaryValue(
      mapOf(
        "Option 1" to 20.0,
        "Option 2" to 80.0
      )
    ))
    val data3 = TeamMetricData(team3, OptionPercentageSummaryValue(
      mapOf(
        "Option 1" to 30.0,
        "Option 2" to 70.0
      )
    ))
    val data = listOf(data2, data3, data1)
    val sorted = data.sortedBy { it.sortValue(AnalyzeSortMode.Ascending, "Option 1") }

    val expected = listOf(
      data1, data2, data3
    )
    assertEquals(expected, sorted)
  }

  @Test
  fun `option value sort with missing option`() {
    val data1 = TeamMetricData(team1, OptionPercentageSummaryValue(
      mapOf(
        "Option 1" to 10.0,
        "Option 2" to 90.0
      )
    ))
    val data2 = TeamMetricData(team2, OptionPercentageSummaryValue(
      mapOf(
        "Option X" to 20.0,
        "Option 2" to 80.0
      )
    ))
    val data3 = TeamMetricData(team3, OptionPercentageSummaryValue(
      mapOf(
        "Option 1" to 30.0,
        "Option 2" to 70.0
      )
    ))
    val data = listOf(data2, data3, data1)
    val sorted = data.sortedBy { it.sortValue(AnalyzeSortMode.Ascending, "Option 1") }

    val expected = listOf(
      // Data 2 will map to "0.0" since "Option 1" is missing
      data2, data1, data3
    )
    assertEquals(expected, sorted)
  }
  @Test
  fun `no sort for string lists`() {
    val data = listOf(
      TeamMetricData(team2, RawStringListSummaryValue(listOf("B"))),
      TeamMetricData(team3, RawStringListSummaryValue(listOf("C"))),
      TeamMetricData(team1, RawStringListSummaryValue(listOf("A"))),
    )
    val sorted = data.sortedBy { it.sortValue(AnalyzeSortMode.Ascending, null) }

    val expected = listOf(
      TeamMetricData(team2, RawStringListSummaryValue(listOf("B"))),
      TeamMetricData(team3, RawStringListSummaryValue(listOf("C"))),
      TeamMetricData(team1, RawStringListSummaryValue(listOf("A"))),
    )
    assertEquals(expected, sorted)
  }

  private fun generateTeam(number: Int) = TeamAtEvent(
    number = number.toString(),
    name = "Team $number",
    eventId = 0
  )

}