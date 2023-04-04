package com.team2052.frckrawler.ui.server.metrics

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MatchMetricsViewModel @Inject constructor(
    //private val matchMetricsDao: MatchMetricsDao
): ViewModel() {
//    var matchMetrics: List<MatchMetric> by mutableStateOf(emptyList())
//
//    fun loadMatchMetrics() {
//        viewModelScope.launch {
//            matchMetrics = matchMetricsDao.getAll()
//        }
//    }
//
//    fun makeMatchMetric(name: String) {
//        viewModelScope.launch {
//            matchMetricsDao.insert(MatchMetric(name = name))
//        }
//        loadMatchMetrics()
//    }
}