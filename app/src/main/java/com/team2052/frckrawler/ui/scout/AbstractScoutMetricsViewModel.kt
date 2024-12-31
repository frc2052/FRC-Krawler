package com.team2052.frckrawler.ui.scout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team2052.frckrawler.data.local.MetricDao
import com.team2052.frckrawler.data.local.MetricDatum
import com.team2052.frckrawler.data.local.MetricDatumDao
import com.team2052.frckrawler.data.local.MetricDatumGroup
import com.team2052.frckrawler.data.local.TeamAtEvent
import com.team2052.frckrawler.data.local.TeamAtEventDao
import com.team2052.frckrawler.data.local.transformer.toMetric
import com.team2052.frckrawler.data.model.MetricState
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import java.time.ZonedDateTime

@OptIn(ExperimentalCoroutinesApi::class)
abstract class AbstractScoutMetricsViewModel(
  private val metricDao: MetricDao,
  private val metricDatumDao: MetricDatumDao,
  private val teamDao: TeamAtEventDao,
) : ViewModel() {
  protected val teams = MutableStateFlow<List<TeamAtEvent>>(emptyList())
  protected val currentTeam = MutableStateFlow<TeamAtEvent?>(null)

  // map is metric ID -> value
  private val pendingData = MutableStateFlow<Map<String, String>>(emptyMap())

  // TODO don't like this
  private val metricSetId = CompletableDeferred<Int>()
  private val eventId = CompletableDeferred<Int>()

  protected fun setMetricSetId(id: Int) {
    metricSetId.complete(id)

    viewModelScope.launch {
      populatePendingDataWithDefaults()
    }
  }

  protected abstract val metricData: SharedFlow<List<MetricDatum>>
  protected abstract fun getDatumGroup(): MetricDatumGroup

  protected open fun getDatumGroupNumber(): Int = 0

  private val metrics = flow { emit(metricSetId.await()) }
    .flatMapLatest { metricDao.getMetrics(it) }
    .map { metricRecords ->
      metricRecords.map { record -> record.toMetric() }
    }
    .shareIn(
      viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000),
      replay = 1
    )

  fun loadTeamsForEvent(
    eventId: Int,
  ) {
    this.eventId.complete(eventId)
    viewModelScope.launch {
      teamDao.getAllTeams(eventId).collect {
        teams.value = it

        if (currentTeam.value == null || !it.contains(currentTeam.value)) {
          currentTeam.value = teams.value.firstOrNull()
        }
      }
    }
  }

  fun updateTeam(team: TeamAtEvent) {
    currentTeam.value = team
  }

  // TODO just need value and metric ID, not whole object
  fun updateMetricState(metricState: MetricState) {
    pendingData.value = pendingData.value.toMutableMap().apply {
      set(metricState.metric.id, metricState.value)
    }
  }

  fun saveMetricData() {
    viewModelScope.launch {

      currentTeam.value?.let { team ->
        val dataWriteJobs = pendingData.value.map { (metricId, value) ->
          async {
            val currentData = metricData.first()

            val datumId = currentData.firstOrNull { it.metricId == metricId }?.id ?: 0
            val datum = MetricDatum(
              id = datumId,
              value = value,
              lastUpdated = ZonedDateTime.now(),
              group = getDatumGroup(),
              groupNumber = getDatumGroupNumber(),
              teamNumber = team.number,
              metricId = metricId,
              eventId = eventId.await()
            )
            metricDatumDao.insert(datum)
          }
        }

        dataWriteJobs.awaitAll()
      }
    }
  }

  protected fun getMetricStates(): Flow<List<MetricState>> {
    return combine(
      metrics,
      metricData,
      pendingData,
    ) { metrics, data, pendingData ->
      metrics.map { metric ->
        val value = if (pendingData.containsKey(metric.id)) {
          pendingData[metric.id]
        } else {
          data.firstOrNull { it.metricId == metric.id }?.value
        }
        MetricState(
          metric = metric,
          value = value ?: metric.defaultValue()
        )
      }
    }
  }

  private suspend fun populatePendingDataWithDefaults() {
    combine(
      metrics,
      metricData,
    ) { metrics, data ->
      metrics.filterNot { metric ->
        // Exclude any metric _without_ saved data
        data.any { it.metricId == metric.id }
      }
    }.collect { metricsWithoutData ->
      pendingData.value = metricsWithoutData.associateBy(
        keySelector = { metric -> metric.id },
        valueTransform = { metric -> metric.defaultValue() }
      )
    }
  }
}