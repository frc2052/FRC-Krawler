package com.team2052.frckrawler.ui.metrics

import com.team2052.frckrawler.data.local.MetricCategory
import com.team2052.frckrawler.data.local.MetricType

data class MetricBuilder(
    val gameId: Int,
    val category: MetricCategory,
    val name: String = "",
    val type: MetricType? = null,
    val priority: Int = 0,
    val enabled: Boolean = true,
    val options: TypeOptions = TypeOptions.None
) {
    val isValid: Boolean = name.isNotBlank()
            && type != null
            && options.isValid
}

sealed class TypeOptions {
    abstract val isValid: Boolean

    object None: TypeOptions() {
        override val isValid: Boolean = true
    }

    data class StringList(val options: List<String> = emptyList()): TypeOptions() {
        override val isValid: Boolean = options.isNotEmpty()
    }

    data class IntRange(val range: kotlin.ranges.IntRange = 0..1): TypeOptions(){
        override val isValid: Boolean = !range.isEmpty()
    }

    data class SteppedIntRange(
        val range: kotlin.ranges.IntRange = 0..1,
        val step: Int = 1,
    ): TypeOptions(){
        override val isValid: Boolean = step > 0 && !range.isEmpty()
    }
}