package com.team2052.frckrawler.ui.metrics.edit

import com.team2052.frckrawler.data.model.Metric

sealed class AddEditMetricMode {
    data object New: AddEditMetricMode()
    data class Edit(val metric: Metric): AddEditMetricMode()
}