package com.team2052.frckrawler.ui.metrics.list

import com.team2052.frckrawler.data.model.Metric

sealed class MetricListScreenState {
    data object Loading: MetricListScreenState()
    data class Content(
        val metrics: List<Metric> = emptyList(),
        val gameName: String = ""
    ): MetricListScreenState()
}