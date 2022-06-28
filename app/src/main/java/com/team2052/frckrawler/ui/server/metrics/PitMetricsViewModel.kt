package com.team2052.frckrawler.ui.server.metrics

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team2052.frckrawler.data.local.PitMetric
import com.team2052.frckrawler.data.local.PitMetricsDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PitMetricsViewModel @Inject constructor(
    private val pitMetricsDao: PitMetricsDao
): ViewModel() {
    var pitMetrics: List<PitMetric> by mutableStateOf(emptyList())

    fun loadPitMetrics() {
        viewModelScope.launch {
            pitMetrics = pitMetricsDao.getAll()
        }
    }

    fun makePitMetric(name: String) {
        viewModelScope.launch {
            pitMetricsDao.insert(PitMetric(name = name))
        }
        loadPitMetrics()
    }
}