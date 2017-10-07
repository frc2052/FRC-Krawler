package com.team2052.frckrawler.metric.data

import com.google.gson.JsonObject
import com.team2052.frckrawler.metric.MetricTypes
import com.team2052.frckrawler.models.Metric
import com.team2052.frckrawler.models.Robot


data class CompiledMetricValue(val robot: Robot? = null, val metric: Metric, val metricValues: List<MetricValue>, val jsonValue: JsonObject) {
    fun getReadableValue(): String {
        return MetricTypes.getType(metric.type).convertCompiledValueToString(jsonValue)
    }

    fun toRobotCompiledValue(robot: Robot): CompiledMetricValue {
        if (this.robot != null) {
            return this
        } else {
            return copy(robot)
        }
    }
}
