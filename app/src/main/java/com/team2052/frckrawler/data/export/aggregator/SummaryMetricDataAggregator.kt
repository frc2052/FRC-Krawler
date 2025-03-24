package com.team2052.frckrawler.data.export.aggregator

import com.team2052.frckrawler.data.export.CsvSummaryDataRow
import com.team2052.frckrawler.data.local.MetricDatum
import com.team2052.frckrawler.data.local.TeamAtEvent
import com.team2052.frckrawler.data.model.Metric
import com.team2052.frckrawler.data.summary.getSummarizer

/**
 * Summarizes data such that each team has a single row of data
 * in the final CSV.
 *
 * Each metric type is summarized differently. Numeric metrics
 * for example are condensed into a single average value
 */
class SummaryMetricDataAggregator(
  private val teamsByNumber: Map<String, TeamAtEvent>
) : MetricDataAggregator<CsvSummaryDataRow> {
  override fun aggregate(
    metrics: List<Metric>,
    data: List<MetricDatum>
  ): List<CsvSummaryDataRow> {
    // Get a list of all data points for each team
    val dataByTeam = data.groupBy {
      teamsByNumber[it.teamNumber] ?: TeamAtEvent(
        number = it.teamNumber,
        name = "Unknown",
        eventId = 0
      )
    }

    // Get the summarized (e.g. averaged) values for each metric,
    // then output a a CsvSummaryDataRow for each team
    return dataByTeam.map { (team, teamData) ->
      val metricValues = metrics.map { metric ->
        val metricData = teamData.filter { it.metricId == metric.id }
        getSummaryValue(metric, metricData)
      }
      CsvSummaryDataRow(
        teamAtEvent = team,
        data = metricValues
      )
    }
  }

  private fun getSummaryValue(
    metric: Metric,
    data: List<MetricDatum>,
  ): String {
    return metric.getSummarizer()
      .summarize(metric, data)
      .asDisplayString()
  }

}