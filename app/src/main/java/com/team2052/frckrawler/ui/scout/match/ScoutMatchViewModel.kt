package com.team2052.frckrawler.ui.scout.match

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team2052.frckrawler.data.local.MetricDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScoutMatchViewModel @Inject constructor(
    private val metricDao: MetricDao
): ViewModel() {

    fun loadMetrics(
        metricSetId: Int,
        matchNumber: Int,
        teamNumber: String,
    ) {
        viewModelScope.launch {
            combine(
                metricDao.getMetrics(metricSetId)
            ) { metrics ->

            }
        }
    }
}