package com.team2052.frckrawler.ui.analyze.team

import com.team2052.frckrawler.data.local.Event
import com.team2052.frckrawler.data.model.Metric
import com.team2052.frckrawler.data.summary.SummaryValue

sealed class TeamDataScreenState{
  data class Content(
    val teamNumber: String,
    val teamName: String,
    val data: Map<Event, Map<Metric, SummaryValue>>,
  ) : TeamDataScreenState()
  data object Loading : TeamDataScreenState()
  data object NoData : TeamDataScreenState()
}