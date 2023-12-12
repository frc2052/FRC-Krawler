package com.team2052.frckrawler.ui.metrics.list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team2052.frckrawler.data.local.MetricCategory
import com.team2052.frckrawler.data.model.Metric
import com.team2052.frckrawler.repository.MetricRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MetricsListViewModel @Inject constructor(
    private val metricRepo: MetricRepository
): ViewModel() {
    var metrics: List<Metric> by mutableStateOf(emptyList())

    fun loadMatchMetrics(
        category: MetricCategory,
        gameId: Int
    ) {
        viewModelScope.launch {
            metricRepo.getGameMetrics(category, gameId).collect() { latestMetrics ->
                metrics = latestMetrics
            }
        }
    }
}