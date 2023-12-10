package com.team2052.frckrawler.ui.metrics

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team2052.frckrawler.data.local.MetricCategory
import com.team2052.frckrawler.data.local.MetricType
import com.team2052.frckrawler.data.model.Metric
import com.team2052.frckrawler.repository.MetricRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddMetricViewModel @Inject constructor(
    private val metricRepo: MetricRepository
): ViewModel() {

    var name = mutableStateOf("")
    var type = mutableStateOf<MetricType?>(null)


    val isStateValid = derivedStateOf {
        type.value != null && name.value.isNotBlank()
    }

    fun saveMetric(
        metric: Metric,
        category: MetricCategory,
        gameId: Int,
    ) {
        viewModelScope.launch {
            val priority = metricRepo.getMetricCountForCategory(category, gameId)
            metricRepo.saveMetric(metric, gameId)
        }
    }
}