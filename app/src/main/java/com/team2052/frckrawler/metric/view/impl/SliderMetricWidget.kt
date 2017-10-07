package com.team2052.frckrawler.metric.view.impl

import android.content.Context
import android.support.v7.widget.AppCompatSeekBar
import android.view.View
import android.widget.TextView

import com.google.gson.JsonElement
import com.jakewharton.rxbinding.widget.RxSeekBar
import com.team2052.frckrawler.R
import com.team2052.frckrawler.data.tba.v3.JSON
import com.team2052.frckrawler.helpers.metric.MetricDataHelper
import com.team2052.frckrawler.metric.data.MetricValue
import com.team2052.frckrawler.metric.view.MetricWidget

import rx.android.schedulers.AndroidSchedulers

class SliderMetricWidget : MetricWidget {
    internal var value: Int = 0
    private var min: Int = 0
    private var max: Int = 0
    private var seekBar: AppCompatSeekBar? = null
    private var valueText: TextView? = null
    private var nameText: TextView? = null
    private var minText: TextView? = null
    private var maxText: TextView? = null

    @Suppress("unused")
    constructor(context: Context, metricValue: MetricValue) : super(context, metricValue) {
        setMetricValue(metricValue)
    }

    constructor(context: Context) : super(context) {}

    override fun setMetricValue(m: MetricValue) {
        min = 0
        max = 1
        nameText!!.text = m.metric.name

        val range = JSON.getAsJsonObject(m.metric.data)
        min = range.get("min").asInt
        max = range.get("max").asInt

        seekBar!!.max = max - min

        minText!!.text = Integer.toString(min)
        maxText!!.text = Integer.toString(max)

        if (m.value != null && !m.value.asJsonObject.get("value").isJsonNull)
            value = m.value.asJsonObject.get("value").asInt
        else
            value = min

        if (value < min || value > max)
            value = min
        seekBar!!.progress = value - min
        valueText!!.text = Integer.toString(value)
    }

    override fun initViews() {
        inflater.inflate(R.layout.widget_metric_slider, this)
        seekBar = findViewById<View>(R.id.sliderVal) as AppCompatSeekBar
        valueText = findViewById<View>(R.id.value) as TextView
        nameText = findViewById<View>(R.id.name) as TextView
        minText = findViewById<View>(R.id.min) as TextView
        maxText = findViewById<View>(R.id.max) as TextView

        RxSeekBar.userChanges(seekBar!!).subscribeOn(AndroidSchedulers.mainThread()).subscribe { seekValue ->
            value = seekValue!! + min
            valueText!!.text = Integer.toString(value)
        }
    }

    override val data: JsonElement get() = MetricDataHelper.buildNumberMetricValue(value)
}
