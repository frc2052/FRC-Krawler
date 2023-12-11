package com.team2052.frckrawler.ui.metrics

sealed class MetricOptions {
    abstract val isValid: Boolean

    data object None: MetricOptions() {
        override val isValid: Boolean = true
    }

    data class StringList(val options: List<String> = emptyList()): MetricOptions() {
        override val isValid: Boolean = options.isNotEmpty()
    }

    data class IntRange(val range: kotlin.ranges.IntRange = 0..10): MetricOptions(){
        override val isValid: Boolean = !range.isEmpty()
    }

    data class SteppedIntRange(
        val range: kotlin.ranges.IntRange = 0..10,
        val step: Int = 1,
    ): MetricOptions(){
        override val isValid: Boolean = step > 0 && !range.isEmpty()
    }
}