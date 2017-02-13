package com.team2052.frckrawler.metric

import android.view.View
import com.google.common.collect.Lists
import com.team2052.frckrawler.db.Metric
import com.team2052.frckrawler.metric.types.*
import com.team2052.frckrawler.metrics.view.impl.*
import com.team2052.frckrawler.tba.JSON

object MetricTypeEntryHandler {
    var booleanMetricTypeWidget: MetricTypeEntry<*>? = null
    var counterMetricTypeWidget: MetricTypeEntry<*>? = null
    var sliderMetricTypeWidget: MetricTypeEntry<*>? = null
    var chooserMetricTypeWidget: MetricTypeEntry<*>? = null
    var checkBoxMetricTypeWidget: MetricTypeEntry<*>? = null
    var stopWatchMetricTypeWidget: MetricTypeEntry<*>? = null
    var textInputMetricTypeWidget: MetricTypeEntry<*>? = null
    internal var metricTypeEntryList: MutableList<MetricTypeEntry<*>> = Lists.newArrayList<MetricTypeEntry<*>>()

    internal fun addMetricEntry(typeEntry: MetricTypeEntry<*>) {
        metricTypeEntryList.add(typeEntry)
    }

    fun getId(typeEntry: MetricTypeEntry<*>): Int = metricTypeEntryList.indexOf(typeEntry)

    fun getTypeEntry(type: Int): MetricTypeEntry<*> = metricTypeEntryList[type]

    fun init() {
        booleanMetricTypeWidget = BooleanMetricTypeEntry()
        counterMetricTypeWidget = object : IntegerMetricType<CounterMetricWidget>(CounterMetricWidget::class.java) {
            override fun incrementationVisibility(): Int = View.VISIBLE

            override fun addInfo(metric: Metric, info: MutableMap<String, String>) {
                super.addInfo(metric, info)
                val data = JSON.getAsJsonObject(metric.data)
                info.put("Incrementation", data.get("inc").toString())
            }
        }
        sliderMetricTypeWidget = IntegerMetricType(SliderMetricWidget::class.java)
        chooserMetricTypeWidget = StringIndexMetricTypeEntry(ChooserMetricWidget::class.java)
        checkBoxMetricTypeWidget = StringIndexMetricTypeEntry(CheckBoxMetricWidget::class.java)
        stopWatchMetricTypeWidget = DoubleMetricTypeEntry(StopwatchMetricWidget::class.java)
        textInputMetricTypeWidget = StringMetricTypeEntry(TextFieldMetricWidget::class.java)

        addMetricEntry(booleanMetricTypeWidget!!)
        addMetricEntry(counterMetricTypeWidget!!)
        addMetricEntry(sliderMetricTypeWidget!!)
        addMetricEntry(chooserMetricTypeWidget!!)
        addMetricEntry(checkBoxMetricTypeWidget!!)
        addMetricEntry(stopWatchMetricTypeWidget!!)
        addMetricEntry(textInputMetricTypeWidget!!)

    }
}
