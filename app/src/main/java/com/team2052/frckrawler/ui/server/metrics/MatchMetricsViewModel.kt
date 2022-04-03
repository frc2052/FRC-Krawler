package com.team2052.frckrawler.ui.server.metrics

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team2052.frckrawler.data.local.Metric
import com.team2052.frckrawler.data.local.MetricsDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MatchMetricsViewModel @Inject constructor(
    private val metricsDao: MetricsDao
): ViewModel() {
    var metrics: List<Metric> by mutableStateOf(emptyList())

    fun loadMetrics() {
        viewModelScope.launch {
            metrics = metricsDao.getAll()
        }
    }

    fun makeMetric(name: String) {
        viewModelScope.launch {
            metricsDao.insert(Metric(name = name))
        }
        loadMetrics()
    }
}