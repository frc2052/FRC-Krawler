package com.team2052.frckrawler.data.export.converter

import com.team2052.frckrawler.data.export.CsvRawDataRow
import com.team2052.frckrawler.data.local.MetricDatumGroup
import com.team2052.frckrawler.data.model.Metric

class RawMetricsCsvRowConverter(
  private val includeTeamNames: Boolean,
): CsvRowConverter<CsvRawDataRow> {
  override fun getHeader(metrics: List<Metric>): String {
    val labels = mutableListOf("\"Team Number\"")
    if (includeTeamNames) {
      labels += "\"Team Name\""
    }
    labels += "\"Type\""
    labels += "\"Match Number\""

    labels += metrics.map { "\"${it.name}\"" }

    return labels.joinToString(separator = ",", postfix = "\n")
  }

  override fun getDataRow(
    csvDataRow: CsvRawDataRow,
  ): String {
    val values = mutableListOf(
      "\"${csvDataRow.teamAtEvent.number}\""
    )
    if (includeTeamNames) {
      values += "\"${csvDataRow.teamAtEvent.name}\""
    }
    values += when (csvDataRow.group) {
      MetricDatumGroup.Match -> "\"Match\""
      MetricDatumGroup.Pit -> "\"Pit\""
    }
    values += "\"${csvDataRow.groupNumber}\""

    values += csvDataRow.data.map { data ->
      data?.let { "\"${it.value}\"" } ?: ""
    }

    return values.joinToString(",", postfix = "\n")
  }
}