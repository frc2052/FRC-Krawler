package com.team2052.frckrawler.ui.metrics.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team2052.frckrawler.data.local.Game
import com.team2052.frckrawler.data.local.GameDao
import com.team2052.frckrawler.data.local.MetricSet
import com.team2052.frckrawler.data.local.MetricSetDao
import com.team2052.frckrawler.data.model.Metric
import com.team2052.frckrawler.repository.MetricRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MetricsListViewModel @Inject constructor(
  private val metricRepo: MetricRepository,
  private val metricSetDao: MetricSetDao,
  private val gameDao: GameDao,
) : ViewModel() {
  private val _state = MutableStateFlow<MetricListScreenState>(MetricListScreenState.Loading)
  val state: StateFlow<MetricListScreenState> = _state

  private lateinit var metricSet: MetricSet
  private lateinit var game: Game

  fun loadMetrics(metricSetId: Int) {
    viewModelScope.launch {
      metricSet = metricSetDao.get(metricSetId)

      combine(
        metricRepo.getMetrics(metricSetId),
        gameDao.getWithUpdates(metricSet.gameId)
      ) { metrics, game ->
        this@MetricsListViewModel.game = game
        MetricListScreenState.Content(
          metrics = metrics,
          setName = metricSet.name,
          gameName = game.name,
          isPitMetricSet = metricSetId == game.pitMetricsSetId,
          isMatchMetricSet = metricSetId == game.matchMetricsSetId,
        )
      }.collect {
        _state.value = it
      }
    }
  }

  fun deleteMetricSet() {
    viewModelScope.launch {
      var updatedGame: Game? = game
      if (game.pitMetricsSetId == metricSet.id) {
        updatedGame = game.copy(pitMetricsSetId = null)
      }
      if (game.matchMetricsSetId == metricSet.id) {
        updatedGame = game.copy(matchMetricsSetId = null)
      }
      if (updatedGame != null && updatedGame != game) {
        gameDao.insert(updatedGame)
      }

      metricSetDao.delete(metricSet)
    }
  }

  fun updateMetricsOrder(metrics: List<Metric>) {
    viewModelScope.launch {
      metricRepo.updatePriorities(metrics)
    }
  }

  fun setIsMatchMetrics(state: Boolean) {
    viewModelScope.launch {
      val updatedGame = if (state) {
        game.copy(matchMetricsSetId = metricSet.id)
      } else {
        game.copy(matchMetricsSetId = null)
      }
      gameDao.insert(updatedGame)
    }
  }

  fun setIsPitMetrics(state: Boolean) {
    viewModelScope.launch {
      val updatedGame = if (state) {
        game.copy(pitMetricsSetId = metricSet.id)
      } else {
        game.copy(pitMetricsSetId = null)
      }
      gameDao.insert(updatedGame)
    }
  }
}