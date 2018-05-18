package com.team2052.frckrawler.core.metrics

import com.google.common.base.Optional
import com.google.common.collect.Lists
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.team2052.frckrawler.core.common.MetricHelper
import com.team2052.frckrawler.core.common.v3.JSON
import com.team2052.frckrawler.core.data.models.MatchDatum
import com.team2052.frckrawler.core.data.models.Metric
import com.team2052.frckrawler.core.data.models.PitDatum
import com.team2052.frckrawler.core.metrics.data.MetricValue
import rx.functions.Func1

object MetricDataHelper {
    var mapPitDataToMetricValue: Func1<PitDatum, MetricValue> = Func1 { pitData: PitDatum -> MetricValue(pitData.metric, JSON.getAsJsonObject(pitData.getData())) }
    var mapMatchDataToMetricValue: Func1<MatchDatum, MetricValue> = Func1 { matchDatum: MatchDatum ->
        val value = JSON.getAsJsonObject(matchDatum.data)
        //Add match number for compiling purposes
        value.addProperty("match_number", matchDatum.match_number)
        MetricValue(matchDatum.metric, value)
    }

    private val listType = object : TypeToken<List<Int>>() {}.type

    private fun getMetricValue(metricValue: MetricValue?): Optional<JsonElement> {
        if (metricValue == null)
            return Optional.absent<JsonElement>()
        if (metricValue.value == null)
            return Optional.absent<JsonElement>()
        return Optional.of(metricValue.value)
    }

    private fun getMetricData(metric: Metric?): Optional<JsonElement> {
        if (metric == null)
            return Optional.absent<JsonElement>()
        if (metric.data == null)
            return Optional.absent<JsonElement>()
        return Optional.of<JsonElement>(JSON.getAsJsonObject(metric.data))
    }

    fun getDoubleMetricValue(metricValue: MetricValue): com.team2052.frckrawler.core.common.Tuple2<Double, ReturnResult> {
        if (metricValue.metricType != MetricHelper.STOP_WATCH
                && metricValue.metricType != MetricHelper.COUNTER
                && metricValue.metricType != MetricHelper.SLIDER)
            return com.team2052.frckrawler.core.common.Tuple2(-1.0, ReturnResult.WRONG_METRIC_TYPE)

        val optional = getMetricValue(metricValue)
        if (!optional.isPresent)
            return com.team2052.frckrawler.core.common.Tuple2(-1.0, ReturnResult.ABSENT_VALUE)

        val value = optional.get().asJsonObject
        if (value.has("value") && !value.get("value").isJsonNull) {
            try {
                return com.team2052.frckrawler.core.common.Tuple2(value.get("value").asDouble, ReturnResult.SUCCEED)
            } catch (e: ClassCastException) {
                return com.team2052.frckrawler.core.common.Tuple2(-1.0, ReturnResult.WRONG_TYPE_VALUE)
            }

        }
        return com.team2052.frckrawler.core.common.Tuple2(-1.0, ReturnResult.OTHER_ERROR)
    }

    fun getListIndexMetricValue(metricValue: MetricValue): com.team2052.frckrawler.core.common.Tuple2<List<Int>, ReturnResult> {
        if (metricValue.metricType != MetricHelper.CHECK_BOX && metricValue.metricType != MetricHelper.CHOOSER)
            return com.team2052.frckrawler.core.common.Tuple2(Lists.newArrayList<Int>(), ReturnResult.WRONG_METRIC_TYPE)

        val optionalValue = getMetricValue(metricValue)
        if (!optionalValue.isPresent)
            return com.team2052.frckrawler.core.common.Tuple2(Lists.newArrayList<Int>(), ReturnResult.ABSENT_VALUE)

        var array: List<Int>? = null
        val valueJson = optionalValue.get().asJsonObject
        if (valueJson.has("values") && !valueJson.get("values").isJsonNull) {
            array = JSON.getGson().fromJson<List<Int>>(valueJson.get("values"), listType)
            return com.team2052.frckrawler.core.common.Tuple2<List<Int>, ReturnResult>(array, ReturnResult.SUCCEED)
        }
        return com.team2052.frckrawler.core.common.Tuple2(Lists.newArrayList<Int>(), ReturnResult.OTHER_ERROR)
    }

    fun getStringMetricValue(metricValue: MetricValue): com.team2052.frckrawler.core.common.Tuple2<String, ReturnResult> {
        if (metricValue.metricType != MetricHelper.TEXT_FIELD)
            return com.team2052.frckrawler.core.common.Tuple2<String, ReturnResult>(null, ReturnResult.WRONG_METRIC_TYPE)

        val optionalValue = getMetricValue(metricValue)
        if (!optionalValue.isPresent)
            return com.team2052.frckrawler.core.common.Tuple2<String, ReturnResult>(null, ReturnResult.ABSENT_VALUE)

        val array: List<Int>? = null
        val valueJson = optionalValue.get().asJsonObject
        if (valueJson.has("value") && !valueJson.get("value").isJsonNull) {
            val text = valueJson.get("value").asString
            return com.team2052.frckrawler.core.common.Tuple2(text, ReturnResult.SUCCEED)
        }
        return com.team2052.frckrawler.core.common.Tuple2<String, ReturnResult>(null, ReturnResult.OTHER_ERROR)
    }

    fun getIntMetricValue(metricValue: MetricValue): com.team2052.frckrawler.core.common.Tuple2<Int, ReturnResult> {
        if (metricValue.metricType != MetricHelper.SLIDER && metricValue.metricType != MetricHelper.COUNTER)
            return com.team2052.frckrawler.core.common.Tuple2(-1, ReturnResult.WRONG_METRIC_TYPE)

        val optional = getMetricValue(metricValue)
        if (!optional.isPresent)
            return com.team2052.frckrawler.core.common.Tuple2(-1, ReturnResult.ABSENT_VALUE)

        val value = optional.get().asJsonObject
        if (value.has("value") && !value.get("value").isJsonNull) {
            try {
                return com.team2052.frckrawler.core.common.Tuple2(value.get("value").asInt, ReturnResult.SUCCEED)
            } catch (e: ClassCastException) {
                return com.team2052.frckrawler.core.common.Tuple2(-1, ReturnResult.WRONG_TYPE_VALUE)
            }

        }
        return com.team2052.frckrawler.core.common.Tuple2(-1, ReturnResult.OTHER_ERROR)
    }

    fun getMatchNumberFromMetricValue(metricValue: MetricValue): Int? {
        val optional = getMetricValue(metricValue)
        if (!optional.isPresent)
            return null
        val value = optional.get().asJsonObject
        if (value.has("match_number") && !value.get("match_number").isJsonNull) {
            try {
                return value.get("match_number").asInt
            } catch (e: ClassCastException) {
                return null
            }

        }
        return null
    }

    fun getBooleanValue(metricValue: MetricValue): Boolean? {
        val optionalValue = getMetricValue(metricValue)
        if (!optionalValue.isPresent)
            return null
        val value = optionalValue.get().asJsonObject
        return getBooleanValue(value)
    }

    fun getBooleanValue(json: JsonObject?): Boolean? {
        if (json == null)
            return null

        if (json.has("value") && !json.get("value").isJsonNull) {
            try {
                return json.get("value").asBoolean
            } catch (e: ClassCastException) {
                return null
            }

        }
        return null
    }

    fun getListItemIndexRange(metric: Metric): Optional<List<String>> {
        if (metric.type != MetricHelper.CHECK_BOX && metric.type != MetricHelper.CHOOSER)
            return Optional.absent<List<String>>()

        val optionalData = getMetricData(metric)
        if (!optionalData.isPresent)
            return Optional.absent<List<String>>()

        val dataJson = optionalData.get().asJsonObject
        if (dataJson.has("values") && !dataJson.get("values").isJsonNull && dataJson.get("values").isJsonArray) {
            val values = dataJson.get("values").asJsonArray
            val range = Lists.newArrayList<String>()
            for (value in values) range.add(value.asString)
            return Optional.of<List<String>>(range)
        }
        return Optional.absent<List<String>>()
    }

    fun buildBooleanMetricValue(value: Boolean): JsonObject {
        val json = JsonObject()
        json.addProperty("value", value)
        return json
    }

    fun buildStringMetricValue(value: String): JsonObject {
        val json = JsonObject()
        json.addProperty("value", value)
        return json
    }

    fun getBooleanMetricValue(metricValue: MetricValue): com.team2052.frckrawler.core.common.Tuple2<Boolean, ReturnResult> {
        if (metricValue.metricType != MetricHelper.BOOLEAN)
            return com.team2052.frckrawler.core.common.Tuple2(false, ReturnResult.WRONG_METRIC_TYPE)

        val optional = getMetricValue(metricValue)
        if (!optional.isPresent)
            return com.team2052.frckrawler.core.common.Tuple2(false, ReturnResult.ABSENT_VALUE)

        val value = optional.get().asJsonObject
        if (value.has("value") && !value.get("value").isJsonNull) {
            try {
                return com.team2052.frckrawler.core.common.Tuple2(value.get("value").asBoolean, ReturnResult.SUCCEED)
            } catch (e: ClassCastException) {
                return com.team2052.frckrawler.core.common.Tuple2(false, ReturnResult.WRONG_TYPE_VALUE)
            }

        }
        return com.team2052.frckrawler.core.common.Tuple2(false, ReturnResult.OTHER_ERROR)
    }

    fun buildNumberMetricValue(value: Number): JsonObject {
        val data = JsonObject()
        data.addProperty("value", value)
        return data
    }

    fun buildListIndexValue(index_data: List<Int>): JsonObject {
        val data = JsonObject()
        val values = JSON.getGson().toJsonTree(index_data)
        data.add("values", values)
        return data
    }

    /**
     * Get the weight for the current data
     */
    fun getCompileWeightForMatchNumber(metricValue: MetricValue, metricData: List<MetricValue>, compileWeight: Double): Double {
        return Math.pow(compileWeight, (metricData.indexOf(metricValue) + 1).toDouble())
    }

    enum class ReturnResult private constructor(var isError: Boolean) {
        SUCCEED(false),
        ABSENT_VALUE(true),
        WRONG_METRIC_TYPE(true),
        WRONG_TYPE_VALUE(true),
        OTHER_ERROR(true)
    }
}
