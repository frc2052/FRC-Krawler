package com.team2052.frckrawler.metric.types

import com.google.common.base.Joiner
import com.google.common.collect.Lists
import com.google.gson.JsonObject
import com.team2052.frckrawler.database.metric.MetricValue
import com.team2052.frckrawler.db.Metric
import com.team2052.frckrawler.db.Robot
import com.team2052.frckrawler.metric.MetricTypeEntry
import com.team2052.frckrawler.metrics.view.MetricWidget
import com.team2052.frckrawler.tba.JSON
import com.team2052.frckrawler.util.MetricHelper

class StringMetricTypeEntry<out W : MetricWidget>(widgetType: Class<W>) : MetricTypeEntry<W>(widgetType) {
    override fun compileValues(robot: Robot, metric: Metric, metricData: List<MetricValue>, compileWeight: Double): JsonObject {
        val json = JsonObject()
        if (metricData.size == 1) {
            val value = MetricHelper.getStringMetricValue(metricData[0])
            val number = MetricHelper.getMatchNumberFromMetricValue(metricData[0])
            if (value.t2.isError) {
                return json
            }
            if (!number.t2.isError) {
                json.addProperty("match_number", number.t1)
            }
            json.addProperty("value", value.t1)

        } else {
            //Match Type
            val map = Lists.newArrayList<JsonObject>()
            for (metricValue in metricData) {
                val parsedValue = MetricHelper.getStringMetricValue(metricValue)

                val number = MetricHelper.getMatchNumberFromMetricValue(metricValue)
                if (parsedValue.t2.isError || number.t2.isError)
                    continue

                val valueJson = JsonObject()
                valueJson.addProperty("match_number", number.t1)
                valueJson.addProperty("value", parsedValue.t1)

                map.add(valueJson)
            }

            json.add("values", JSON.getGson().toJsonTree(map).asJsonArray)
        }

        return json
    }

    override fun buildMetric(name: String, min: Int?, max: Int?, inc: Int?, commaList: List<String>?): MetricHelper.MetricFactory {
        val factory = MetricHelper.MetricFactory(name)
        factory.setMetricType(typeId)
        return factory
    }

    override fun convertValueToString(value: JsonObject): String {
        if (value.has("value") && !value.get("value").isJsonNull) {
            var valueString = ""
            if (value.has("match_number") && !value.get("match_number").isJsonNull) {
                valueString += "${value.get("match_number").asInt}: "
            }
            valueString += value.get("value").asString
            return valueString
        }
        val values = Lists.newArrayList<String>()
        value.getAsJsonArray("values")
                .map { it.asJsonObject }
                .mapTo(values) { "${it.get("match_number")}: ${it.get("value").asString}" }
        return Joiner.on(", ").join(values)
    }
}