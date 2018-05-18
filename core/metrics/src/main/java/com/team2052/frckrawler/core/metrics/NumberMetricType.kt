package com.team2052.frckrawler.core.metrics

import com.google.gson.JsonObject
import com.team2052.frckrawler.core.data.models.Metric
import com.team2052.frckrawler.core.metrics.data.CompiledMetricValue
import com.team2052.frckrawler.core.metrics.data.MetricValue
import com.team2052.frckrawler.core.metrics.view.MetricWidget

abstract class NumberMetricType<W : MetricWidget> : MetricType<W>() {
    override fun compile(metric: Metric, metricValues: List<MetricValue>, weight: Float): CompiledMetricValue {
        val compiledValue = JsonObject()

        if (metricValues.isEmpty()) {
            compiledValue.addProperty("value", 0.0)
            return CompiledMetricValue(metric = metric, metricValues = metricValues, jsonValue = compiledValue)
        }

        var numerator = 0.0
        var denominator = 0.0

        for (value in metricValues) {
            val result = MetricDataHelper.getDoubleMetricValue(value)

            if (result.t2.isError)
                continue

            val compileWeightForMatchNumber = MetricDataHelper.getCompileWeightForMatchNumber(value, metricValues, weight.toDouble())
            numerator += result.t1 * compileWeightForMatchNumber
            denominator += compileWeightForMatchNumber
        }
        val value = format.format(numerator / denominator)
        compiledValue.addProperty("value", value)
        return CompiledMetricValue(metric = metric, metricValues = metricValues, jsonValue = compiledValue)
    }

    override fun convertCompiledValueToString(jsonObject: JsonObject): String {
        return jsonObject.get("value").asString
    }
}
