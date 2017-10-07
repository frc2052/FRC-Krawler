package com.team2052.frckrawler.metric

import android.content.Context

import com.google.gson.JsonObject
import com.team2052.frckrawler.helpers.metric.MetricHelper
import com.team2052.frckrawler.metric.data.CompiledMetricValue
import com.team2052.frckrawler.metric.data.MetricValue
import com.team2052.frckrawler.metric.view.MetricWidget
import com.team2052.frckrawler.models.Metric

import java.text.DecimalFormat

abstract class MetricType<W : MetricWidget> {

    protected abstract fun widgetClass(): Class<W>

    @get:MetricHelper.MetricType
    abstract val type: Int

    fun createWidget(context: Context, metricValue: MetricValue): W? {
        try {
            return widgetClass().getDeclaredConstructor(Context::class.java, MetricValue::class.java).newInstance(context, metricValue)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

    }

    fun createWidget(context: Context): W? {
        try {
            return widgetClass().getDeclaredConstructor(Context::class.java).newInstance(context)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

    }

    abstract fun compile(metric: Metric, metricValues: List<MetricValue>, weight: Float): CompiledMetricValue

    abstract fun convertCompiledValueToString(jsonObject: JsonObject): String

    companion object {
        val format = DecimalFormat("0.00")
    }
}
