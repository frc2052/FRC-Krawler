package com.team2052.frckrawler.core.metrics.data

import com.google.gson.JsonElement
import com.team2052.frckrawler.core.common.MetricHelper
import com.team2052.frckrawler.core.common.v3.JSON
import com.team2052.frckrawler.core.data.models.Metric

data class MetricValue(val metric: Metric, val value: JsonElement?) {
    val metricType: Int
        @MetricHelper.MetricType
        get() = metric.type

    fun valueAsString(): String {
        return if (value == null) "" else JSON.getGson().toJson(value)
    }
}