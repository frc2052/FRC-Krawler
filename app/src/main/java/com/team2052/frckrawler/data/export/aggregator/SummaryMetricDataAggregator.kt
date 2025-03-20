package com.team2052.frckrawler.data.export.aggregator

import com.team2052.frckrawler.data.export.CsvSummaryDataRow
import com.team2052.frckrawler.data.export.summary.BooleanMetricSummarizer
import com.team2052.frckrawler.data.export.summary.FullStringMetricSummarizer
import com.team2052.frckrawler.data.export.summary.NumericMetricSummarizer
import com.team2052.frckrawler.data.export.summary.StringOptionsMetricSummarizer
import com.team2052.frckrawler.data.local.MetricDatum
import com.team2052.frckrawler.data.local.MetricRecord
import com.team2052.frckrawler.data.local.MetricType
import com.team2052.frckrawler.data.local.TeamAtEvent

class SummaryMetricDataAggregator(
  private val teamsByNumber: Map<String, TeamAtEvent>
) : MetricDataAggregator<CsvSummaryDataRow> {
  override fun aggregate(
    metrics: List<MetricRecord>,
    data: List<MetricDatum>
  ): List<CsvSummaryDataRow> {
    val dataByTeam = data.groupBy {
      teamsByNumber[it.teamNumber] ?: TeamAtEvent(
        number = it.teamNumber,
        name = "Unknown",
        eventId = 0
      )
    }

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
    metric: MetricRecord,
    data: List<MetricDatum>,
  ): String {
    val summarizer = when (metric.type) {
      MetricType.Boolean -> BooleanMetricSummarizer
      MetricType.Counter -> NumericMetricSummarizer
      MetricType.Slider -> NumericMetricSummarizer
      MetricType.Chooser -> StringOptionsMetricSummarizer
      MetricType.Checkbox -> StringOptionsMetricSummarizer
      MetricType.Stopwatch -> StringOptionsMetricSummarizer
      MetricType.TextField -> FullStringMetricSummarizer
    }
    return summarizer.summarize(data)
  }

}