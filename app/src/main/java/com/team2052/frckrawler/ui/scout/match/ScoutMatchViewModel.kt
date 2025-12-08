package com.team2052.frckrawler.ui.scout.match

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team2052.frckrawler.data.local.MetricDao
import com.team2052.frckrawler.data.local.MetricDatum
import com.team2052.frckrawler.data.local.MetricDatumDao
import com.team2052.frckrawler.data.local.MetricDatumGroup
import com.team2052.frckrawler.data.local.TeamAtEventDao
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import dev.zacsweers.metro.AppScope
import com.team2052.frckrawler.ui.scout.AbstractScoutMetricsViewModel
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
@ContributesIntoMap(AppScope::class, binding = binding<ViewModel>())
@ViewModelKey(ScoutMatchViewModel::class)
@Inject
class ScoutMatchViewModel(
  metricDao: MetricDao,
  teamDao: TeamAtEventDao,
  private val metricDatumDao: MetricDatumDao,
) : AbstractScoutMetricsViewModel(metricDao, metricDatumDao, teamDao) {

  private val _state = MutableStateFlow<ScoutMatchScreenState?>(null)
  val state: StateFlow<ScoutMatchScreenState?> = _state

  private val currentMatch = MutableStateFlow(1)
  private val eventId: MutableStateFlow<Int?> = MutableStateFlow(null)

  override val metricData: SharedFlow<List<MetricDatum>> = combine(
    currentMatch,
    currentTeam.filterNotNull(),
    eventId.filterNotNull(),
  ) { match, team, eventId ->
    Triple(match, team, eventId)
  }.flatMapLatest { (match, team, eventId) ->
    metricDatumDao.getTeamDatumForMatchMetrics(
      matchNumber = match,
      teamNumber = team.number,
      eventId
    )
  }.shareIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5_000),
    replay = 1,
  )

  override fun getDatumGroup(): MetricDatumGroup = MetricDatumGroup.Match
  override fun getDatumGroupNumber(): Int = currentMatch.value

  fun loadMetricsAndTeams(
    metricSetId: Int,
    eventId: Int,
  ) {
    setMetricSetId(metricSetId)
    this.eventId.value = eventId
    loadTeamsForEvent(eventId)

    viewModelScope.launch {
      combine(
        teams,
        currentTeam.filterNotNull(),
        currentMatch,
        getMetricStates(),
      ) { teams, currentTeam, currentMatch, metricStates ->
        ScoutMatchScreenState(
          matchInformation = MatchInformationState(
            matchNumber = currentMatch,
            teams = teams,
            selectedTeam = currentTeam
          ),
          metricStates = metricStates
        )
      }.collect {
        _state.value = it
      }
    }
  }

  fun updateMatchNumber(match: Int) {
    currentMatch.value = match
  }
}