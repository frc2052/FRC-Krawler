package com.team2052.frckrawler.ui.analyze

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team2052.frckrawler.data.local.EventDao
import com.team2052.frckrawler.data.local.GameDao
import com.team2052.frckrawler.data.local.MetricDatum
import com.team2052.frckrawler.data.local.MetricDatumDao
import com.team2052.frckrawler.data.local.TeamAtEventDao
import com.team2052.frckrawler.data.model.Metric
import com.team2052.frckrawler.data.summary.BooleanMetricSummarizer
import com.team2052.frckrawler.data.summary.FullStringMetricSummarizer
import com.team2052.frckrawler.data.summary.NumericMetricSummarizer
import com.team2052.frckrawler.data.summary.StringOptionsMetricSummarizer
import com.team2052.frckrawler.data.summary.SummaryValue
import com.team2052.frckrawler.repository.MetricRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnalyzeDataViewModel @Inject constructor(
  private val gameDao: GameDao,
  private val eventDao: EventDao,
  private val teamAtEventDao: TeamAtEventDao,
  private val metricDataDao: MetricDatumDao,
  private val metricRepository: MetricRepository,
) : ViewModel() {
  // TODO replace these with saved state handle args
  private val gameIdFlow = MutableSharedFlow<Int>(replay = 1)
  private val eventIdFlow = MutableSharedFlow<Int>(replay = 1)

  private val game = gameIdFlow.map { gameDao.get(it) }
    .shareIn(viewModelScope, replay = 1, started = SharingStarted.Lazily)
  private val event = eventIdFlow.map { eventDao.get(it) }
    .shareIn(viewModelScope, replay = 1, started = SharingStarted.Lazily)

  private val metrics = game.map {
    val pitMetrics = it.pitMetricsSetId?.let { pitMetricsSetId ->
      metricRepository.getMetrics(pitMetricsSetId).first()
    } ?: emptyList()
    val matchMetrics = it.matchMetricsSetId?.let { matchMetricsSetId ->
      metricRepository.getMetrics(matchMetricsSetId).first()
    } ?: emptyList()

    val eventMetrics = pitMetrics + matchMetrics

    // Sorting on a text field doesn't make much sense in our context
    return@map eventMetrics.filterNot { metric -> metric is Metric.TextFieldMetric }
  }
    .shareIn(viewModelScope, replay = 1, started = SharingStarted.Lazily)

  private val sortMode = MutableStateFlow(AnalyzeSortMode.Ascending)
  private val selectedMetricOption = MutableStateFlow<String?>(null)
  private val selectedMetricId = MutableStateFlow<String?>(null)
  private val selectedMetric = combine(metrics, selectedMetricId) { metrics, selectedMetricId ->
    metrics.find { it.id == selectedMetricId } ?: metrics.first()
  }

  private val teams = eventIdFlow.map { eventId ->
    teamAtEventDao.getAllTeams(eventId).first()
  }.shareIn(viewModelScope, replay = 1, started = SharingStarted.Lazily)

  private val eventData = eventIdFlow.map { eventId ->
    metricDataDao.getEventMatchData(eventId) + metricDataDao.getEventPitData(eventId)
  }.shareIn(viewModelScope, replay = 1, started = SharingStarted.Lazily)

  private val filterState = combine(
    sortMode, metrics, selectedMetric, selectedMetricOption
  ) { sortMode, metrics, selectedMetric, selectedMetricOption ->
    SortFilterState(
      sortMode = sortMode,
      availableMetrics = metrics,
      selectedMetric = selectedMetric,
      selectedMetricOption = selectedMetricOption
    )
  }

  private val teamData: Flow<List<TeamMetricData>> = combine(
    teams, eventData, filterState,
  ) { teams, data, filterState ->
    val teamSummaries = teams.map { team ->
      val teamMetricData = data.filter {
        it.teamNumber == team.number && it.metricId == filterState.selectedMetric.id
      }
      val summary = getSummaryValue(filterState.selectedMetric, teamMetricData)

      TeamMetricData(
        team = team,
        summary = summary,
      )
    }

    teamSummaries.sortedBy {
      it.sortValue(filterState.sortMode, filterState.selectedMetricOption)
    }
  }

  val state: StateFlow<AnalyzeDataScreenState> = combine(
    game, event, filterState, teamData,
  ) { game, event, filterState, teamData ->
    AnalyzeDataScreenState.Content(
      gameName = game.name,
      eventName = event.name,
      teamData = teamData,
      filterState = filterState,
    )
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5_000),
    initialValue = AnalyzeDataScreenState.Loading
  )


  fun loadGameAndEvent(gameId: Int, eventId: Int) {
    viewModelScope.launch {
      gameIdFlow.emit(gameId)
      eventIdFlow.emit(eventId)
    }
  }

  fun setSortMode(mode: AnalyzeSortMode) {
    viewModelScope.launch {
      sortMode.value = mode
    }
  }

  fun setSelectedMetric(metricId: String) {
    viewModelScope.launch {
      selectedMetricId.value = metricId
    }
  }

  fun setSelectedMetricOption(option: String?) {
    viewModelScope.launch {
      selectedMetricOption.value = option
    }
  }

  private fun getSummaryValue(
    metric: Metric,
    data: List<MetricDatum>,
  ): SummaryValue {
    val summarizer = when (metric) {
      is Metric.BooleanMetric -> BooleanMetricSummarizer
      is Metric.CounterMetric -> NumericMetricSummarizer
      is Metric.SliderMetric -> NumericMetricSummarizer
      is Metric.ChooserMetric -> StringOptionsMetricSummarizer
      is Metric.CheckboxMetric -> StringOptionsMetricSummarizer
      is Metric.StopwatchMetric -> StringOptionsMetricSummarizer
      is Metric.TextFieldMetric -> FullStringMetricSummarizer
    }
    return summarizer.summarize(data)
  }
}