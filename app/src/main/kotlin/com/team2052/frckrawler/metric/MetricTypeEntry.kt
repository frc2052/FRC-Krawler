package com.team2052.frckrawler.metric

import android.content.Context
import android.view.View
import com.google.gson.JsonObject
import com.team2052.frckrawler.database.metric.MetricValue
import com.team2052.frckrawler.db.Metric
import com.team2052.frckrawler.db.Robot
import com.team2052.frckrawler.util.MetricHelper
import com.team2052.frckrawler.metrics.view.MetricWidget

abstract class MetricTypeEntry<out W : MetricWidget> internal constructor(private val widgetType: Class<W>) {

    init {
        MetricTypeEntryHandler.addMetricEntry(this)
    }

    fun getWidget(c: Context, v: MetricValue): W? {
        try {
            return widgetType.getDeclaredConstructor(Context::class.java, MetricValue::class.java).newInstance(c, v)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    fun getWidget(c: Context): W? {
        try {
            return widgetType.getDeclaredConstructor(Context::class.java).newInstance(c)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    abstract fun convertValueToString(value: JsonObject): String

    open fun minimumVisibility(): Int = View.GONE
    open fun maximumVisibility(): Int = View.GONE
    open fun incrementationVisibility(): Int = View.GONE
    open fun commaListVisibility(): Int = View.GONE

    val typeId: Int
        get() = MetricTypeEntryHandler.getId(this)

    open fun addInfo(metric: Metric, info: MutableMap<String, String>) {
    }
    abstract fun compileValues(robot: Robot, metric: Metric, metricData: List<MetricValue>, compileWeight: Double): JsonObject
    abstract fun buildMetric(
            name: String,
            min: Int?,
            max: Int?,
            inc: Int?,
            commaList: List<String>?): MetricHelper.MetricFactory
}
