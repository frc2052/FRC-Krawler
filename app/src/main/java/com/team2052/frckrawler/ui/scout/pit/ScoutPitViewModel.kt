package com.team2052.frckrawler.ui.scout.pit

import androidx.lifecycle.viewModelScope
import com.team2052.frckrawler.data.local.MetricDao
import com.team2052.frckrawler.data.local.MetricDatum
import com.team2052.frckrawler.data.local.MetricDatumDao
import com.team2052.frckrawler.data.local.MetricDatumGroup
import com.team2052.frckrawler.data.local.TeamAtEventDao
import com.team2052.frckrawler.ui.scout.AbstractScoutMetricsViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ScoutPitViewModel @Inject constructor(
    metricDao: MetricDao,
    teamDao: TeamAtEventDao,
    private val metricDatumDao: MetricDatumDao,
): AbstractScoutMetricsViewModel(metricDao, metricDatumDao, teamDao) {

    private val _state = MutableStateFlow<ScoutPitScreenState?>(null)
    val state: StateFlow<ScoutPitScreenState?> = _state

    override fun getDatumGroup(): MetricDatumGroup = MetricDatumGroup.Pit

    fun loadMetricsAndTeams(
        metricSetId: Int,
        eventId: Int,
    ) {
        setMetricSetId(metricSetId)
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

    override fun getMetricData(): Flow<List<MetricDatum>> {
        return currentTeam.filterNotNull().
            flatMapLatest { team ->
            metricDatumDao.getDatumForPitMetrics(
                teamNumber = team.number
            )
        }
    }

}