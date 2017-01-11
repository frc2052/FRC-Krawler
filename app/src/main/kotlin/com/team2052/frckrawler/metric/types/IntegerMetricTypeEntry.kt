package com.team2052.frckrawler.metric.types

import android.view.View
import com.google.gson.JsonObject
import com.team2052.frckrawler.database.metric.CompiledMetricValue
import com.team2052.frckrawler.database.metric.MetricValue
import com.team2052.frckrawler.db.Metric
import com.team2052.frckrawler.db.Robot
import com.team2052.frckrawler.metric.MetricTypeEntry
import com.team2052.frckrawler.tba.JSON
import com.team2052.frckrawler.util.MetricHelper
import com.team2052.frckrawler.metrics.view.MetricWidget

open class IntegerMetricType<out W : MetricWidget>(widgetType: Class<W>) : MetricTypeEntry<W>(widgetType) {
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
            val result = MetricHelper.getIntMetricValue(metricValue)

            if (result.t2.isError)
                continue

            val weight = CompiledMetricValue.getCompileWeightForMatchNumber(metricValue, metricData, compileWeight)
            numerator += result.t1 * weight
            denominator += weight
        }

        val value = CompiledMetricValue.format.format(numerator / denominator)
        compiledValue.addProperty("value", value)
        return compiledValue
    }

    override fun buildMetric(name: String, min: Int?, max: Int?, inc: Int?, commaList: List<String>?): MetricHelper.MetricFactory {
        val metricFactory = MetricHelper.MetricFactory(name)
        metricFactory.setMetricType(this.typeId)
        metricFactory.setDataMinMaxInc(min!!, max!!, inc)
        return metricFactory
    }

    override fun addInfo(metric: Metric, info: MutableMap<String, String>) {
        val data = JSON.getAsJsonObject(metric.data)
        info.put("Minimum", data.get("min").toString())
        info.put("Maximum", data.get("max").toString())
    }

    override fun commaListVisibility(): Int = View.GONE
    override fun maximumVisibility(): Int = View.VISIBLE
    override fun minimumVisibility(): Int = View.VISIBLE
}