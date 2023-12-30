package com.team2052.frckrawler.ui.metrics.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team2052.frckrawler.data.local.MetricCategory
import com.team2052.frckrawler.data.local.MetricSet
import com.team2052.frckrawler.data.local.MetricSetDao
import com.team2052.frckrawler.repository.MetricRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MetricsListViewModel @Inject constructor(
    private val metricRepo: MetricRepository,
    private val metricSetDao: MetricSetDao,
): ViewModel() {
    private val _state = MutableStateFlow<MetricListScreenState>(MetricListScreenState.Loading)
    val state: StateFlow<MetricListScreenState> = _state

    private lateinit var metricSet: MetricSet

    fun loadMetrics(
        category: MetricCategory,
        metricSetId: Int
    ) {
        viewModelScope.launch {
            metricSet = metricSetDao.get(metricSetId)
            metricRepo.getMetrics(category, metricSetId).collect { metrics ->
                _state.value = MetricListScreenState.Content(
                    metrics = metrics,
                    setName = metricSet.name
                )
            }
        }
    }

    fun deleteMetricSet() {
        viewModelScope.launch {
            metricSetDao.delete(metricSet)
        }
    }
}