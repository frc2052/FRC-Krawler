package com.team2052.frckrawler.data.export.converter

import com.team2052.frckrawler.data.export.CsvSummaryDataRow
import com.team2052.frckrawler.data.local.MetricRecord

class SummaryMetricsCsvRowConverter(
  private val includeTeamNames: Boolean,
): CsvRowConverter<CsvSummaryDataRow> {
  override fun getHeader(metrics: List<MetricRecord>): String {
    val labels = mutableListOf("\"Team Number\"")
    if (includeTeamNames) {
      labels += "\"Team Name\""
    }

    labels += metrics.map { "\"${it.name}\"" }

    return labels.joinToString(separator = ",", postfix = "\n")
  }

  override fun getDataRow(
    csvDataRow: CsvSummaryDataRow,
  ): String {
    val values = mutableListOf(
      "\"${csvDataRow.teamAtEvent.number}\""
    )
    if (includeTeamNames) {
      values += "\"${csvDataRow.teamAtEvent.name}\""
    }

    values += csvDataRow.data.map { it?.let { "\"$it\"" } ?: "" }

    return values.joinToString(",", postfix = "\n")
  }
}