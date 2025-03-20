package com.team2052.frckrawler.data.export

import com.team2052.frckrawler.data.local.TeamAtEvent

data class CsvSummaryDataRow(
  val teamAtEvent: TeamAtEvent,
  val data: List<String?>
)