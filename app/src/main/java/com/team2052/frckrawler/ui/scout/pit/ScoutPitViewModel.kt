package com.team2052.frckrawler.ui.scout.pit

import androidx.lifecycle.viewModelScope
import com.team2052.frckrawler.data.local.MetricDao
import com.team2052.frckrawler.data.local.MetricDatumDao
import com.team2052.frckrawler.data.local.MetricDatumGroup
import com.team2052.frckrawler.data.local.TeamAtEventDao
import com.team2052.frckrawler.di.viewmodel.ViewModelKey
import com.team2052.frckrawler.di.viewmodel.ViewModelScope
import com.team2052.frckrawler.ui.scout.AbstractScoutMetricsViewModel
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
@ContributesIntoMap(ViewModelScope::class)
@ViewModelKey(ScoutPitViewModel::class)
@Inject
class ScoutPitViewModel(
  metricDao: MetricDao,
  teamDao: TeamAtEventDao,
  private val metricDatumDao: MetricDatumDao,
) : AbstractScoutMetricsViewModel(metricDao, metricDatumDao, teamDao) {

  private val _state = MutableStateFlow<ScoutPitScreenState?>(null)
  val state: StateFlow<ScoutPitScreenState?> = _state

  private val eventId: MutableStateFlow<Int?> = MutableStateFlow(null)

  override val metricData = combine(
    currentTeam.filterNotNull(),
    eventId.filterNotNull(),
  ) { team, eventId ->
    Pair(team, eventId)
  }.flatMapLatest { (team, eventId) ->
    metricDatumDao.getDatumForPitMetrics(
      teamNumber = team.number,
      eventId = eventId,
    )
  }.shareIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5_000),
    replay = 1,
  )

  override fun getDatumGroup(): MetricDatumGroup = MetricDatumGroup.Pit

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
        getMetricStates(),
      ) { teams, currentTeam, metricStates ->
        ScoutPitScreenState(
          availableTeams = teams,
          selectedTeam = currentTeam,
          metricStates = metricStates
        )
      }.collect {
        _state.value = it
      }
    }
  }
}