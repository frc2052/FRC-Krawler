package com.team2052.frckrawler.ui.metrics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team2052.frckrawler.data.local.MetricCategory
import com.team2052.frckrawler.data.local.MetricType
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

    private var gameId: Int = -1
    private lateinit var category: MetricCategory


    private val _state = MutableStateFlow(AddEditMetricScreenState())
    val state: StateFlow<AddEditMetricScreenState> = _state

    fun startEditingNewMetric(gameId: Int, category: MetricCategory) {
        this.gameId = gameId
        this.category = category
    }

    fun updateName(name: String) {
        // TODO add some visible validation messaging?
        _state.value = _state.value.copy(name = name)
    }

    fun updateType(type: MetricType) {
        val typeOptions = when (type) {
            MetricType.Slider -> TypeOptions.IntRange()
            MetricType.Counter -> TypeOptions.SteppedIntRange()
            MetricType.Chooser, MetricType.Checkbox -> TypeOptions.StringList()
            else -> TypeOptions.None
        }
        val newState = _state.value.copy(
            type = type,
            options = typeOptions
        )

        // TODO there's gotta be a better way than copying twice
        _state.value = newState.copy(
            previewMetric = newState.toMetric(
                id = -1,
                category = MetricCategory.Match,
                priority = -1
            )
        )
    }

    fun updateOptions(typeOptions: TypeOptions) {
        // TODO validate that options type matches metrics type?
        _state.value = _state.value.copy(
            options = typeOptions
        )
    }

    fun save() {
        viewModelScope.launch {
            val priority = metricRepo.getMetricCountForCategory(category, gameId)
            val metric = _state.value.toMetric(
                id = 0,
                category = category,
                priority = priority
            )
            // Should only be null if we didn't have a type (and thus save shouldn't be enabled)
            if (metric != null) {
                metricRepo.saveMetric(metric, gameId)
            }
        }
    }
}