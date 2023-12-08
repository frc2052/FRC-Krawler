package com.team2052.frckrawler.ui.server.metrics

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team2052.frckrawler.data.local.Metric
import com.team2052.frckrawler.data.local.MetricCategory
import com.team2052.frckrawler.data.local.MetricDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MetricsListViewModel @Inject constructor(
    private val metricDao: MetricDao
): ViewModel() {
    var metrics: List<Metric> by mutableStateOf(emptyList())

    fun loadMatchMetrics(
        category: MetricCategory,
        gameId: Int
    ) {
        viewModelScope.launch {
            metrics = metricDao.getGameMetricsWithCategory(category, gameId)
        }
    }
}