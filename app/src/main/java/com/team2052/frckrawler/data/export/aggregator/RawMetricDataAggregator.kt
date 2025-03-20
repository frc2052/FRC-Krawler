package com.team2052.frckrawler.data.export.aggregator

import com.team2052.frckrawler.data.export.CsvRawDataRow
import com.team2052.frckrawler.data.local.MetricDatum
import com.team2052.frckrawler.data.local.MetricRecord
import com.team2052.frckrawler.data.local.TeamAtEvent

class RawMetricDataAggregator(
  private val teamsByNumber: Map<String, TeamAtEvent>
) : MetricDataAggregator<CsvRawDataRow> {
  override fun aggregate(
    metrics: List<MetricRecord>,
    data: List<MetricDatum>
  ): List<CsvRawDataRow> {
    // Convert the metric list into a map of metricId to index in sort list
    // for more efficient sorting later
    val metricSortMap = metrics.map { it.id }.indexMap()

    val groupedData = data
      .filter { it.metricId in metricSortMap.keys }
      .groupBy {
        val team = teamsByNumber[it.teamNumber] ?: TeamAtEvent(
          number = it.teamNumber,
          name = "Unknown",
          eventId = 0
        )
        Triple(team, it.group, it.groupNumber)
     }

    return groupedData.map { (key, groupData) ->
      val (team, group, groupNumber) = key
      val rawRows = groupData.toRows(metricSortMap)
      rawRows.map { row ->
        CsvRawDataRow(
          teamAtEvent = team,
          group = group,
          groupNumber = groupNumber,
          data = row
        )
      }
    }.flatten()
  }

  private fun List<MetricDatum>.toRows(
    metricIdSortOrder: Map<String, Int>,
  ): List<List<MetricDatum?>> {
    val groupedByMetric = groupBy { it.metricId }
    val result = mutableListOf<List<MetricDatum?>>()
    var currentGroup = mutableListOf<MetricDatum?>()

    val numberOfRows = groupedByMetric.values.maxOf { it.size }
    (0 until numberOfRows).forEach { row ->
      groupedByMetric.forEach { (_, data) ->
        currentGroup += data.getOrNull(row)
      }
      currentGroup.sortBy { entry -> entry?.let { metricIdSortOrder[it.metricId] } ?: Int.MAX_VALUE }
      result += currentGroup
      currentGroup = mutableListOf()
    }

    return result
  }

  private fun <T> Iterable<T>.indexMap(): Map<T, Int> {
    val map = mutableMapOf<T, Int>()
    forEachIndexed { i, v ->
      map.put(v, i)
    }
    return map
  }

}