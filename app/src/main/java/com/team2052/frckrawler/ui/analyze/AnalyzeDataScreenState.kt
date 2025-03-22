package com.team2052.frckrawler.ui.analyze

import com.team2052.frckrawler.data.local.TeamAtEvent
import com.team2052.frckrawler.data.model.Metric

sealed class AnalyzeDataScreenState{
  data class Content(
    val eventName: String,
    val gameName: String,
    val filterState: SortFilterState,
    val teamData: List<TeamMetricData>,
  ) : AnalyzeDataScreenState()
  data object Loading : AnalyzeDataScreenState()
}

data class SortFilterState(
  val availableMetrics: List<Metric>,
  val selectedMetric: Metric,
  val selectedMetricOption: String?,
  val sortMode: AnalyzeSortMode,
)

data class TeamMetricData(
  val team: TeamAtEvent,
  val data: String,
)

enum class AnalyzeSortMode {
  Ascending,
  Descending
}