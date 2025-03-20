package com.team2052.frckrawler.data.export

import com.team2052.frckrawler.data.local.MetricDatum
import com.team2052.frckrawler.data.local.MetricDatumGroup
import com.team2052.frckrawler.data.local.TeamAtEvent

data class CsvRawDataRow(
  val teamAtEvent: TeamAtEvent,
  val group: MetricDatumGroup,
  val groupNumber: Int,
  val data: List<MetricDatum?>
)