package com.team2052.frckrawler.ui.metrics.list

import com.team2052.frckrawler.data.model.Metric
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

sealed class MetricListScreenState {
  data object Loading : MetricListScreenState()
  data class Content(
    val metrics: ImmutableList<Metric> = persistentListOf(),
    val setName: String = "",
    val gameName: String = "",
    val isPitMetricSet: Boolean = false,
    val isMatchMetricSet: Boolean = false,
  ) : MetricListScreenState()
}