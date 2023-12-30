package com.team2052.frckrawler.ui.metric_set

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team2052.frckrawler.data.local.MetricSet
import com.team2052.frckrawler.data.local.MetricSetDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MetricSetListViewModel @Inject constructor(
    private val metricSetDao: MetricSetDao
): ViewModel() {
    var metricSets: List<MetricSet> by mutableStateOf(emptyList())

    fun loadMetricSets() {
        viewModelScope.launch {
            metricSetDao.getAll().collect {
                metricSets = it
            }
        }
    }

    fun makeMetricSet(name: String) {
        viewModelScope.launch {
            metricSetDao.insert(MetricSet(name = name))
        }
    }

}