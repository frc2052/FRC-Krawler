package com.team2052.frckrawler.ui.metrics.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team2052.frckrawler.data.local.MetricType
import com.team2052.frckrawler.data.model.Metric
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import dev.zacsweers.metro.AppScope
import com.team2052.frckrawler.repository.MetricRepository
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@ContributesIntoMap(AppScope::class)
@ViewModelKey(AddMetricViewModel::class)
@Inject
class AddMetricViewModel(
  private val metricRepo: MetricRepository
) : ViewModel() {

  private var metricId: String = ""
  private var metricSetId: Int = -1

  private val _state = MutableStateFlow(AddEditMetricScreenState())
  val state: StateFlow<AddEditMetricScreenState> = _state

  fun startEditingNewMetric(
    metricId: String,
    metricSetId: Int,
  ) {
    this.metricSetId = metricSetId
    this.metricId = metricId
    _state.value = AddEditMetricScreenState()
  }

  fun startEditingMetric(
    metric: Metric,
    metricSetId: Int
  ) {
    this.metricSetId = metricSetId
    this.metricId = metric.id

    _state.value = AddEditMetricScreenState(
      name = metric.name,
      type = metric.getType(),
      priority = metric.priority,
      enabled = metric.enabled,
      options = metric.getMetricOptions(),
    )
  }

  fun setEnabled(enabled: Boolean) {
    _state.value = _state.value.copy(enabled = enabled)
  }

  fun updateName(name: String) {
    // TODO add some visible validation messaging?
    _state.value = _state.value.copy(name = name)
  }

  fun updateType(type: MetricType) {
    val currentOptions = _state.value.options
    val typeOptions = when (type) {
      MetricType.Slider -> {
        if (currentOptions is MetricOptions.SteppedIntRange) {
          MetricOptions.IntRange(
            range = currentOptions.range
          )
        } else {
          MetricOptions.IntRange()
        }
      }

      MetricType.Counter -> {
        if (currentOptions is MetricOptions.IntRange) {
          MetricOptions.SteppedIntRange(
            range = currentOptions.range
          )
        } else {
          MetricOptions.SteppedIntRange()
        }
      }

      MetricType.Chooser, MetricType.Checkbox -> {
        if (currentOptions is MetricOptions.StringList) {
          currentOptions
        } else {
          MetricOptions.StringList()
        }
      }

      else -> MetricOptions.None
    }
    val newState = _state.value.copy(
      type = type,
      options = typeOptions
    )

    _state.value = newState
  }

  fun updateOptions(metricOptions: MetricOptions) {
    _state.value = _state.value.copy(
      options = metricOptions
    )
  }

  fun save() {
    viewModelScope.launch {
      var shouldShiftDefaultCommentField = false
      val priority = if (_state.value.priority == -1) {
        shouldShiftDefaultCommentField = metricRepo.isDefaultCommentFieldAtEnd(metricSetId)
        val endPriority = metricRepo.getMetricCount(metricSetId) - 1
        if (shouldShiftDefaultCommentField) {
          // New metric goes before the comment field
          endPriority - 1
        } else {
          // No comment field or it is not at the end - new metric goes at end
          endPriority
        }
      } else {
        _state.value.priority
      }
      val metric = _state.value.toMetric(
        id = metricId,
        priority = priority
      )

      metricRepo.saveMetric(metric, metricSetId)

      if (shouldShiftDefaultCommentField) {
        metricRepo.moveDefaultCommentToEnd(metricSetId)
      }
    }
  }

  fun deleteMetric() {
    viewModelScope.launch {
      metricRepo.deleteMetric(metricId)
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