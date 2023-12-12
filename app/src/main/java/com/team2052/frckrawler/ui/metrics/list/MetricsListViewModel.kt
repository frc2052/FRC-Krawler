package com.team2052.frckrawler.ui.metrics.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team2052.frckrawler.data.local.GameDao
import com.team2052.frckrawler.data.local.MetricCategory
import com.team2052.frckrawler.repository.MetricRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MetricsListViewModel @Inject constructor(
    private val metricRepo: MetricRepository,
    private val gameDao: GameDao,
): ViewModel() {
    private val _state = MutableStateFlow<MetricListScreenState>(MetricListScreenState.Loading)
    val state: StateFlow<MetricListScreenState> = _state

    fun loadMatchMetrics(
        category: MetricCategory,
        gameId: Int
    ) {
        viewModelScope.launch {
            val game = gameDao.get(gameId)
            metricRepo.getGameMetrics(category, gameId).collect { metrics ->
                _state.value = MetricListScreenState.Content(
                    metrics = metrics,
                    gameName = game.name
                )
            }
        }
    }
}