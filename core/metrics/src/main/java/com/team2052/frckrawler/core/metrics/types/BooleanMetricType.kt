package com.team2052.frckrawler.core.metrics.types

import com.google.gson.JsonObject
import com.team2052.frckrawler.core.metrics.MetricDataHelper
import com.team2052.frckrawler.core.common.MetricHelper
import com.team2052.frckrawler.core.metrics.MetricType
import com.team2052.frckrawler.core.data.models.Metric
import com.team2052.frckrawler.core.metrics.data.CompiledMetricValue
import com.team2052.frckrawler.core.metrics.data.MetricValue
import com.team2052.frckrawler.core.metrics.view.impl.BooleanMetricWidget

class BooleanMetricType : MetricType<BooleanMetricWidget>() {
    override fun widgetClass(): Class<BooleanMetricWidget> {
        return BooleanMetricWidget::class.java
    }

    override val type: Int get() = MetricHelper.BOOLEAN

    override fun compile(metric: Metric, metricValues: List<MetricValue>, weight: Float): CompiledMetricValue {
        if (metric.type != type) {
            throw IllegalStateException("Metric type must be the same when compiling values!")
        }

        val compiledValue = JsonObject()

        if (metricValues.isEmpty()) {
            compiledValue.addProperty("value", 0.0)
            return CompiledMetricValue(metric = metric, metricValues = metricValues, jsonValue = compiledValue)
        }

        var numerator = 0.0
        var denominator = 0.0

        for (value in metricValues) {
            val result = MetricDataHelper.getBooleanMetricValue(value)

            if (result.t2.isError) {
                continue
            }

            val compileWeightForMatchNumber = MetricDataHelper.getCompileWeightForMatchNumber(value, metricValues, weight.toDouble())

            if (result.t1) {
                numerator += compileWeightForMatchNumber
            } else {
                denominator += compileWeightForMatchNumber
            }
        }

        val value = MetricType.format.format(numerator / (numerator + denominator) * 100)
        compiledValue.addProperty("value", value)
        return CompiledMetricValue(metric = metric, metricValues = metricValues, jsonValue = compiledValue)
    }

    override fun convertCompiledValueToString(jsonObject: JsonObject): String {
        return jsonObject.get("value").asString
    }
}
