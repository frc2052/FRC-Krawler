package com.team2052.frckrawler.ui.metrics.edit

import com.team2052.frckrawler.data.model.Metric
import java.util.UUID

sealed class AddEditMetricMode {
    data class New(val metricId: String = UUID.randomUUID().toString()): AddEditMetricMode()
    data class Edit(val metric: Metric): AddEditMetricMode()
}