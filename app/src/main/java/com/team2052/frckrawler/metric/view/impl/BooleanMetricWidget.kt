package com.team2052.frckrawler.metric.view.impl

import android.content.Context
import android.view.View
import android.view.View.OnClickListener
import android.widget.RadioButton
import com.google.gson.JsonElement
import com.team2052.frckrawler.R
import com.team2052.frckrawler.helpers.metric.MetricDataHelper
import com.team2052.frckrawler.metric.data.MetricValue
import com.team2052.frckrawler.metric.view.MetricWidget
import kotlinx.android.synthetic.main.widget_metric_boolean.view.*

class BooleanMetricWidget : MetricWidget, OnClickListener {
    private var value = false

    @Suppress("unused")
    constructor(context: Context, m: MetricValue) : super(context, m) {
        setMetricValue(m)
    }

    constructor(context: Context) : super(context) {}

    override fun setMetricValue(m: MetricValue) {
        name.text = m.metric.name
        findViewById<View>(R.id.yes).setOnClickListener(this)
        findViewById<View>(R.id.no).setOnClickListener(this)

        val optionalValue = MetricDataHelper.getBooleanValue(m)
        if (optionalValue != null) {
            setValue(optionalValue)
        } else {
            setValue(false)
        }
    }

    override fun initViews() {
        inflater.inflate(R.layout.widget_metric_boolean, this)
    }

    fun setValue(value: Boolean) {
        this.value = value

        yes.isChecked = value
        no.isChecked = !value
    }

    override fun onClick(view: View) {
        val checked = (view as RadioButton).isChecked

        when (view.getId()) {
            R.id.yes -> if (checked)
                value = true
            R.id.no -> if (checked)
                value = false
        }
    }

    override val data: JsonElement get() = MetricDataHelper.buildBooleanMetricValue(value)
}
