package com.team2052.frckrawler.ui.scout.pit

import com.team2052.frckrawler.data.local.TeamAtEvent
import com.team2052.frckrawler.data.model.MetricState

data class ScoutPitScreenState(
  val availableTeams: List<TeamAtEvent>,
  val selectedTeam: TeamAtEvent,
  val metricStates: List<MetricState>,
)