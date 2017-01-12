package com.team2052.frckrawler.metric.types

import android.view.View
import com.google.common.base.Joiner
import com.google.common.base.Strings
import com.google.common.collect.Maps
import com.google.gson.JsonObject
import com.team2052.frckrawler.database.metric.CompiledMetricValue
import com.team2052.frckrawler.database.metric.MetricValue
import com.team2052.frckrawler.db.Metric
import com.team2052.frckrawler.db.Robot
import com.team2052.frckrawler.metric.MetricTypeEntry
import com.team2052.frckrawler.metrics.view.MetricWidget
import com.team2052.frckrawler.tba.JSON
import com.team2052.frckrawler.util.MetricHelper
import com.team2052.frckrawler.util.Tuple2

open class StringIndexMetricTypeEntry<out W : MetricWidget>(widgetType: Class<W>) : MetricTypeEntry<W>(widgetType) {
    override fun convertValueToString(value: JsonObject): String {
        val names = value.get("names").asJsonArray
        val values = value.get("values").asJsonArray
        var value = ""
        for (i in 0..names.size() - 1) {
            value += String.format("%s - %s%s" + if (i == names.size() - 1) "" else "\n", names.get(i).asString, values.get(i).asDouble, '%')
        }
        return value
    }

    override fun compileValues(robot: Robot, metric: Metric, metricData: List<MetricValue>, compileWeight: Double): JsonObject {var denominator = 0.0
        val compiledValue = JsonObject()
        val possible_values = JSON.getAsJsonObject(metric.data).get("values").asJsonArray
        val compiledVal = Maps.newTreeMap<Int, Tuple2<String, Double>>()

        for (i in 0..possible_values.size() - 1) {
            compiledVal.put(i, Tuple2(possible_values.get(i).asString, 0.0))
        }

        if (metricData.isEmpty()) {
            val values = JSON.getGson().toJsonTree(Tuple2.yieldValues(compiledVal.values).toTypedArray()).asJsonArray
            compiledValue.add("names", possible_values)
            compiledValue.add("values", values)
            return compiledValue
        }


        for (metricValue in metricData) {
            val result = MetricHelper.getListIndexMetricValue(metricValue)
            if (result.t2.isError)
                continue

            val weight = CompiledMetricValue.getCompileWeightForMatchNumber(metricValue, metricData, compileWeight)

            for (index in result.t1)
                compiledVal.put(index, compiledVal[index]!!.setT2(compiledVal[index]!!.t2 + weight))

            denominator += weight
        }

        for ((key, value) in compiledVal) {
            compiledVal.put(key, value.setT2(Math.round(value.t2 / denominator * 100 * 100.0) / 100.0))
        }

        val values = JSON.getGson().toJsonTree(Tuple2.yieldValues(compiledVal.values).toTypedArray()).asJsonArray
        compiledValue.add("names", possible_values)
        compiledValue.add("values", values)
        return compiledValue

    }

    override fun addInfo(metric: Metric, info: MutableMap<String, String>) {
        val data = JSON.getAsJsonObject(metric.data)
        val values = Joiner.on(", ").join(data.get("values").asJsonArray)
        info.put("Comma Separated List", if (Strings.isNullOrEmpty(values)) "No Values" else values)
    }

    override fun buildMetric(name: String, min: Int?, max: Int?, inc: Int?, commaList: List<String>?): MetricHelper.MetricFactory {
        val metricFactory = MetricHelper.MetricFactory(name)
        metricFactory.setMetricType(this.typeId)
        metricFactory.setDataListIndexValue(commaList)
        return metricFactory
    }

    override fun commaListVisibility(): Int = View.VISIBLE
}