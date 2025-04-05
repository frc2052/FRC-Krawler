package com.team2052.frckrawler.ui.analyze.team

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team2052.frckrawler.data.local.Event
import com.team2052.frckrawler.data.local.EventDao
import com.team2052.frckrawler.data.local.MetricDatum
import com.team2052.frckrawler.data.local.MetricDatumDao
import com.team2052.frckrawler.data.local.TeamAtEventDao
import com.team2052.frckrawler.data.model.Metric
import com.team2052.frckrawler.data.summary.SummaryValue
import com.team2052.frckrawler.data.summary.getSummarizer
import com.team2052.frckrawler.repository.MetricRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeamDataViewModel @Inject constructor(
  private val eventDao: EventDao,
  private val teamAtEventDao: TeamAtEventDao,
  private val metricDataDao: MetricDatumDao,
  private val metricRepository: MetricRepository,
) : ViewModel() {
  // TODO replace these with saved state handle args
  private val teamNumberFlow = MutableSharedFlow<String>(replay = 1)

  private val teamData = teamNumberFlow.map { getTeamDataByEvent(it) }
    .shareIn(viewModelScope, replay = 1, started = SharingStarted.Lazily)

  private val metricsById = teamData.map {
    val metricIds = it.values.flatten().map { it.metricId }
    val metrics = metricIds.map { metricRepository.getMetric(it) }
    metrics.associateBy { it.id }
  }.shareIn(viewModelScope, replay = 1, started = SharingStarted.Lazily)

  val state: StateFlow<TeamDataScreenState> = combine(
    teamNumberFlow, teamData, metricsById,
  ) { teamNumber, teamData, metricsById ->
    if (metricsById.isEmpty()) {
      return@combine TeamDataScreenState.NoData
    }
    val teamName = teamAtEventDao.getTeamAtEvent(teamData.keys.first().id, teamNumber)
    val summariesByEvent = teamData.mapValues { (event, data) ->
      summarizeTeamData(data, metricsById)
    }

    TeamDataScreenState.Content(
      teamNumber = teamNumber,
      teamName = teamName?.name ?: "",
      data = summariesByEvent
    )
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5_000),
    initialValue = TeamDataScreenState.Loading
  )


  fun loadTeamData(teamNumber: String) {
    viewModelScope.launch {
      teamNumberFlow.emit(teamNumber)
    }
  }

  private suspend fun getTeamDataByEvent(teamNumber: String): Map<Event, List<MetricDatum>> {
    val allData = metricDataDao.getAllTeamDatum(teamNumber)
    val dataByEventId = allData.groupBy { it.eventId }
    return dataByEventId.mapKeys { (id, _) -> eventDao.get(id) }
  }

  private fun summarizeTeamData(
    teamData: List<MetricDatum>,
    metricsById: Map<String, Metric>
  ): Map<Metric, SummaryValue> {
  return teamData.groupBy { it.metricId }
    .filter { (metricId, _) -> metricsById.containsKey(metricId) }
    .mapKeys { (metricId, _) -> metricsById[metricId]!! }
    .mapValues { (metric, data) -> getSummaryValue(metric, data) }
  }

  private fun getSummaryValue(
    metric: Metric,
    data: List<MetricDatum>,
  ): SummaryValue {
    return metric.getSummarizer().summarize(metric, data)
  }
}