package com.team2052.frckrawler.core.metrics.data

import com.google.gson.JsonObject
import com.team2052.frckrawler.core.data.models.Metric
import com.team2052.frckrawler.core.data.models.Team
import com.team2052.frckrawler.core.metrics.MetricTypes


data class CompiledMetricValue(val team: Team? = null, val metric: Metric, val metricValues: List<MetricValue>, val jsonValue: JsonObject) {
    fun getReadableValue(): String {
        return MetricTypes.getType(metric.type).convertCompiledValueToString(jsonValue)
    }

    fun toTeamCompiledValue(robot: Team): CompiledMetricValue {
        if (this.team != null) {
            return this
        } else {
            return copy(robot)
        }
    }
}
