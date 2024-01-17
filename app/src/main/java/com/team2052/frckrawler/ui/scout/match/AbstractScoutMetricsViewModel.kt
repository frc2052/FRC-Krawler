package com.team2052.frckrawler.ui.scout.match

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team2052.frckrawler.data.local.MetricDao
import com.team2052.frckrawler.data.local.MetricDatum
import com.team2052.frckrawler.data.local.MetricDatumDao
import com.team2052.frckrawler.data.local.MetricDatumGroup
import com.team2052.frckrawler.data.local.TeamAtEvent
import com.team2052.frckrawler.data.local.TeamAtEventDao
import com.team2052.frckrawler.data.local.transformer.toMetric
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.ZonedDateTime

abstract class AbstractScoutMetricsViewModel constructor(
    private val metricDao: MetricDao,
    private val metricDatumDao: MetricDatumDao,
    private val teamDao: TeamAtEventDao,
): ViewModel() {
    protected val teams = MutableStateFlow<List<TeamAtEvent>>(emptyList())
    protected val currentTeam = MutableStateFlow<TeamAtEvent?>(null)

    protected abstract fun getMetricData(): Flow<List<MetricDatum>>
    protected abstract fun getDatumGroup(): MetricDatumGroup

    protected open fun getDatumGroupNumber(): Int = 0

    fun loadTeamsForEvent(
        eventId: Int,
    ) {
        viewModelScope.launch {
            teamDao.getAllTeams(eventId).collect {
                teams.value = it

                if (currentTeam.value == null || !it.contains(currentTeam.value)) {
                    currentTeam.value = teams.value.first()
                }
            }
        }
    }

    fun updateTeam(team: TeamAtEvent) {
        currentTeam.value = team
    }

    // TODO just need value and metric ID
    fun updateMetricState(metricState: MetricState) {
        currentTeam.value?.let { team ->
            val datum = MetricDatum(
                value = metricState.value,
                lastUpdated = ZonedDateTime.now(),
                group = getDatumGroup(),
                groupNumber = getDatumGroupNumber(),
                teamNumber = team.number,
                metricId = metricState.metric.id
            )

            viewModelScope.launch {
                metricDatumDao.insert(datum)
            }
        }
    }

    protected fun getMetricStates(metricSetId: Int): Flow<List<MetricState>> {
        return combine(
            metricDao.getMetrics(metricSetId),
            getMetricData()
        ) { metrics, data ->
            metrics.map { metricRecord ->
                val value = data.firstOrNull { it.metricId == metricRecord.id }?.value
                val metric = metricRecord.toMetric()
                MetricState(
                    metric = metric,
                    value = value ?: metric.defaultValue()
                )
            }
        }
    }
}