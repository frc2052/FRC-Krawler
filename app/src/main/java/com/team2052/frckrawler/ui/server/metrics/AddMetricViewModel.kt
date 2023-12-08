package com.team2052.frckrawler.ui.server.metrics

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team2052.frckrawler.data.local.Metric
import com.team2052.frckrawler.data.local.MetricCategory
import com.team2052.frckrawler.data.local.MetricDao
import com.team2052.frckrawler.data.local.MetricType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddMetricViewModel @Inject constructor(
    private val metricDao: MetricDao
): ViewModel() {
    var metrics: List<Metric> by mutableStateOf(emptyList())

    fun saveMetric(
        name: String,
        category: MetricCategory,
        gameId: Int,
        type: MetricType
    ) {
        viewModelScope.launch {
            val priority = metricDao.getMetricCountForCategory(category, gameId)
            metricDao.insert(
                Metric(
                    name = name,
                    category = category,
                    gameId = gameId,
                    type = type,
                    priority = priority,
                    enabled = true
                )
            )
        }
    }
}