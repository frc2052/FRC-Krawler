package com.team2052.frckrawler.ui.metrics.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team2052.frckrawler.data.local.MetricCategory
import com.team2052.frckrawler.data.local.MetricType
import com.team2052.frckrawler.data.model.Metric
import com.team2052.frckrawler.repository.MetricRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddMetricViewModel @Inject constructor(
    private val metricRepo: MetricRepository
): ViewModel() {

    private var metricId: Int = 0
    private var gameId: Int = -1
    private lateinit var category: MetricCategory

    private val _state = MutableStateFlow(AddEditMetricScreenState())
    val state: StateFlow<AddEditMetricScreenState> = _state

    fun startEditingNewMetric(gameId: Int, category: MetricCategory) {
        this.gameId = gameId
        this.category = category
        this.metricId = 0
    }

    fun startEditingMetric(
        metric: Metric,
        gameId: Int,
        category: MetricCategory
    ) {
        this.gameId = gameId
        this.category = category
        this.metricId = metric.id

        _state.value = AddEditMetricScreenState(
            name = metric.name,
            type = metric.getType(),
            priority = metric.priority,
            enabled = metric.enabled,
            options = metric.getMetricOptions(),
        )
    }

    fun updateName(name: String) {
        // TODO add some visible validation messaging?
        _state.value = _state.value.copy(name = name)
    }

    fun updateType(type: MetricType) {
        val typeOptions = when (type) {
            MetricType.Slider -> MetricOptions.IntRange()
            MetricType.Counter -> MetricOptions.SteppedIntRange()
            MetricType.Chooser, MetricType.Checkbox -> MetricOptions.StringList()
            else -> MetricOptions.None
        }
        val newState = _state.value.copy(
            type = type,
            options = typeOptions
        )

        _state.value = newState
    }

    fun updateOptions(metricOptions: MetricOptions) {
        // TODO validate that options type matches metrics type?
        _state.value = _state.value.copy(
            options = metricOptions
        )
    }

    fun save() {
        viewModelScope.launch {
            val priority = metricRepo.getMetricCountForCategory(category, gameId)
            val metric = _state.value.toMetric(
                id = metricId,
                category = category,
                priority = priority
            )

            metricRepo.saveMetric(metric, gameId)
        }
    }

    private fun Metric.getMetricOptions(): MetricOptions {
        return when (this) {
            is Metric.CheckboxMetric -> MetricOptions.StringList(options)
            is Metric.ChooserMetric -> MetricOptions.StringList(options)
            is Metric.CounterMetric -> MetricOptions.SteppedIntRange(
                range = range.first..range.last,
                step = range.step
            )
            is Metric.SliderMetric -> MetricOptions.IntRange(range.first..range.last)
            else -> MetricOptions.None
        }
    }
}