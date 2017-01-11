package com.team2052.frckrawler.metric.types

import com.google.gson.JsonObject
import com.team2052.frckrawler.database.metric.CompiledMetricValue
import com.team2052.frckrawler.database.metric.MetricValue
import com.team2052.frckrawler.db.Metric
import com.team2052.frckrawler.db.Robot
import com.team2052.frckrawler.metric.MetricTypeEntry
import com.team2052.frckrawler.util.MetricHelper
import com.team2052.frckrawler.metrics.view.impl.BooleanMetricWidget

open class BooleanMetricTypeEntry : MetricTypeEntry<BooleanMetricWidget>(BooleanMetricWidget::class.java) {
    override fun convertValueToString(value: JsonObject): String {
        return value.get("value").asDouble.toString()
    }

    override fun compileValues(robot: Robot, metric: Metric, metricData: List<MetricValue>, compileWeight: Double): JsonObject {
        var numerator = 0.0
        var denominator = 0.0
        val compiledValue = JsonObject()

        if (metricData.isEmpty()) {
            compiledValue.addProperty("value", 0.0)
            return compiledValue
        }

        for (metricValue in metricData) {
            val result = MetricHelper.getBooleanMetricValue(metricValue)

            if (result.t2.isError)
                continue

            val weight = CompiledMetricValue.getCompileWeightForMatchNumber(metricValue, metricData, compileWeight)

            if (result.t1) {
                numerator += weight
            } else {
                denominator += weight
            }
        }

        val value = CompiledMetricValue.format.format(numerator / (numerator + denominator) * 100)
        compiledValue.addProperty("value", value)
        return compiledValue
    }

    override fun buildMetric(name: String, min: Int?, max: Int?, inc: Int?, commaList: List<String>?): MetricHelper.MetricFactory {
        val metricFactory = MetricHelper.MetricFactory(name)
        metricFactory.setMetricType(this.typeId)
        return metricFactory
    }
}