package com.team2052.frckrawler.data.export.aggregator

import com.team2052.frckrawler.data.export.CsvRawDataRow
import com.team2052.frckrawler.data.local.MetricDatum
import com.team2052.frckrawler.data.local.TeamAtEvent
import com.team2052.frckrawler.data.model.Metric

/**
 * Aggregate raw metric data for teams for CSV export
 * Each combination of a team in a particular match could have multiple
 * rows of data.
 */
class RawMetricDataAggregator(
  private val teamsByNumber: Map<String, TeamAtEvent>
) : MetricDataAggregator<CsvRawDataRow> {
  override fun aggregate(
    metrics: List<Metric>,
    data: List<MetricDatum>
  ): List<CsvRawDataRow> {
    // Convert the metric list into a map of metricId to index in sort list
    // for more efficient sorting later
    val metricSortMap = metrics.map { it.id }.indexMap()

    // Group the data by team, group, and group number (e.g. a single entry here would have all
    // data for team 123 in match 2)
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

  /**
   * Given a list of datapoints for a team in a particular group (e.g. match 1),
   * outputs rows of data for the final CSV file.
   *
   * Since multiple scouts may have reported data for a single team in a
   * single match, this may result in multiple rows of data for that combination of team/match.
   */
  private fun List<MetricDatum>.toRows(
    metricIdSortOrder: Map<String, Int>,
  ): List<List<MetricDatum?>> {
    val groupedByMetric = groupBy { it.metricId }

    // The minimum number of rows required is equal to the max number
    // of data points we have for any single metric
    val numberOfRows = groupedByMetric.values.maxOf { it.size }

    val result = mutableListOf<List<MetricDatum?>>()
    var currentGroup = mutableListOf<MetricDatum?>()

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

  /**
   * Build an index map where the keys in the map are the elements
   * from a list and the value is the index of that element in the list.
   *
   * This is a convenient cache for looking up the index of an element
   * in the original list
   */
  private fun <T> Iterable<T>.indexMap(): Map<T, Int> {
    val map = mutableMapOf<T, Int>()
    forEachIndexed { i, v ->
      map[v] = i
    }
    return map
  }

}