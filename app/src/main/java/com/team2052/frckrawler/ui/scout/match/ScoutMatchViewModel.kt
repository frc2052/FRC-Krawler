package com.team2052.frckrawler.ui.scout.match

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
class ScoutMatchViewModel @Inject constructor(
    metricDao: MetricDao,
    teamDao: TeamAtEventDao,
    private val metricDatumDao: MetricDatumDao,
): AbstractScoutMetricsViewModel(metricDao, metricDatumDao, teamDao) {

    private val _state = MutableStateFlow<ScoutMatchScreenState?>(null)
    val state: StateFlow<ScoutMatchScreenState?> = _state

    private val currentMatch = MutableStateFlow(1)

    override fun getDatumGroup(): MetricDatumGroup = MetricDatumGroup.Match
    override fun getDatumGroupNumber(): Int = currentMatch.value

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

    override fun getMetricData(): Flow<List<MetricDatum>> {
        return combine(
            currentMatch,
            currentTeam.filterNotNull()
        ) { match, team ->
            Pair(match, team)
        }.flatMapLatest { (match, team) ->
            metricDatumDao.getTeamDatumForMatchMetrics(
                matchNumber = match,
                teamNumber = team.number
            )
        }
    }

}