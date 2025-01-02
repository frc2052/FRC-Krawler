package com.team2052.frckrawler.ui.scout.match

import com.team2052.frckrawler.data.local.TeamAtEvent
import com.team2052.frckrawler.data.model.MetricState

data class ScoutMatchScreenState(
  val matchInformation: MatchInformationState,
  val metricStates: List<MetricState>,
)

data class MatchInformationState(
  val matchNumber: Int,
  val teams: List<TeamAtEvent>,
  val selectedTeam: TeamAtEvent,
)