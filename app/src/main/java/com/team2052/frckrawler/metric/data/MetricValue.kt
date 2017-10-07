package com.team2052.frckrawler.metric.data

import com.google.gson.JsonElement
import com.team2052.frckrawler.helpers.metric.MetricHelper
import com.team2052.frckrawler.models.Metric
import com.team2052.frckrawler.toJsonString

data class MetricValue(val metric: Metric, val value: JsonElement?) {
    val metricType: Int
        @MetricHelper.MetricType
        get() = metric.type

    fun valueAsString(): String {
        return if (value == null) "" else value.toJsonString()
    }
}