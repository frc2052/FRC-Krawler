package com.team2052.frckrawler.metric.view.impl

import android.content.Context
import android.text.InputType
import android.view.View
import android.view.View.OnClickListener
import android.widget.TextView

import com.afollestad.materialdialogs.MaterialDialog
import com.google.gson.JsonElement
import com.team2052.frckrawler.R
import com.team2052.frckrawler.helpers.metric.MetricDataHelper
import com.team2052.frckrawler.metric.data.MetricValue
import com.team2052.frckrawler.metric.getChooserData
import com.team2052.frckrawler.metric.view.MetricWidget
import kotlinx.android.synthetic.main.widget_metric_counter.view.*

class CounterMetricWidget : MetricWidget, OnClickListener, View.OnLongClickListener {
    internal var mValue: Int = 0
    private var max: Int = 0
    private var min: Int = 0
    private var increment: Int = 0

    @Suppress("unused")
    constructor(context: Context, metricValue: MetricValue) : super(context, metricValue) {
        setMetricValue(metricValue)
    }

    constructor(context: Context) : super(context)

    override fun setMetricValue(m: MetricValue) {
        (findViewById<View>(R.id.title) as TextView).text = m.metric.name

        val data = m.metric.getChooserData()

        if (data != null) {
            min = data.min
            max = data.max
            increment = data.inc
        }

        if (m.value != null) {
            mValue = m.value.asJsonObject.get("value").asInt
        } else {
            mValue = min
        }

        value.text = mValue.toString()
    }

    override fun initViews() {
        inflater.inflate(R.layout.widget_metric_counter, this)
        plus.setOnClickListener(this)
        plus.setOnLongClickListener(this)
        minus.setOnClickListener(this)
        minus.setOnLongClickListener(this)
    }

    override fun onClick(v: View) {
        if (v.id == R.id.plus) {
            mValue += increment

            if (mValue > max) {
                mValue = max
            }

        } else if (v.id == R.id.minus) {
            mValue -= increment

            if (mValue < min) {
                mValue = min
            }
        }
        updateValueText()
    }

    private fun updateValueText() {
        value.text = mValue.toString()
    }

    override val data: JsonElement get() = MetricDataHelper.buildNumberMetricValue(mValue)

    override fun onLongClick(v: View): Boolean {
        MaterialDialog.Builder(context)
                .title(title.text.toString())
                .inputType(InputType.TYPE_CLASS_NUMBER)
                .input("Enter Value", mValue.toString(), false) { dialog, input ->
                    val i = Integer.parseInt(input.toString())
                    if (i > max) {
                        mValue = max
                    } else if (i < min) {
                        mValue = min
                    } else {
                        mValue = i
                    }
                    updateValueText()
                }.show()
        return false
    }
}
