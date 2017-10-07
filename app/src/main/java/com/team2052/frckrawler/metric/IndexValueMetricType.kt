package com.team2052.frckrawler.metric

import com.google.common.collect.Maps
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.team2052.frckrawler.data.tba.v3.JSON
import com.team2052.frckrawler.helpers.Tuple2
import com.team2052.frckrawler.helpers.metric.MetricDataHelper
import com.team2052.frckrawler.metric.data.CompiledMetricValue
import com.team2052.frckrawler.metric.data.MetricValue
import com.team2052.frckrawler.metric.view.ListIndexMetricWidget
import com.team2052.frckrawler.models.Metric

abstract class IndexValueMetricType<M : ListIndexMetricWidget> : MetricType<M>() {
    private fun compiledValueToJson(compiledMap: Map<Int, Tuple2<String, Double>>, possible_values: JsonArray): JsonObject {
        val compiledValue = JsonObject()
        val values = JSON.getGson().toJsonTree(Tuple2.yieldValues(compiledMap.values).toTypedArray()).asJsonArray
        compiledValue.add("names", possible_values)
        compiledValue.add("values", values)
        return compiledValue
    }

    override fun compile(metric: Metric, metricValues: List<MetricValue>, weight: Float): CompiledMetricValue {
        val possibleValues = JSON.getAsJsonObject(metric.data).get("values").asJsonArray
        val compiledMap = Maps.newTreeMap<Int, Tuple2<String, Double>>()
        var denominator = 0.0

        for (i in 0..possibleValues.size() - 1) {
            compiledMap.put(i, Tuple2(possibleValues.get(i).asString, 0.0))
        }

        if (metricValues.isEmpty()) {
            return CompiledMetricValue(
                    metric = metric,
                    metricValues = metricValues,
                    jsonValue = compiledValueToJson(compiledMap, possibleValues)
            )
        }

        for (i in metricValues.indices) {
            val value = metricValues[i]
            val value_result = MetricDataHelper.getListIndexMetricValue(value)
            if (value_result.t2.isError) {
                continue
            }

            val weightForMatch = MetricDataHelper.getCompileWeightForMatchNumber(value, metricValues, weight.toDouble())

            for (index in value_result.t1) {
                if (!compiledMap.containsKey(index)) {
                    continue
                }

                val newValue = compiledMap[index]?.t2?.plus(weightForMatch)
                compiledMap.put(index, compiledMap[index]?.setT2(newValue))
            }

            denominator += weightForMatch
        }

        for ((key, value) in compiledMap) {
            val percent = Math.round(value.t2 / denominator * 100 * 100.0) / 100.0
            compiledMap.put(key, value.setT2(percent))
        }

        return CompiledMetricValue(
                metric = metric,
                metricValues = metricValues,
                jsonValue = compiledValueToJson(compiledMap, possibleValues))
    }

    override fun convertCompiledValueToString(jsonObject: JsonObject): String {
        val names = jsonObject.get("names").asJsonArray
        val values = jsonObject.get("values").asJsonArray
        val value = StringBuilder()
        for (i in 0..names.size() - 1) {
            value.append(String.format("%s:%s%s\n", names.get(i).asString, values.get(i).asDouble, '%'))
        }
        return value.toString()
    }
}
