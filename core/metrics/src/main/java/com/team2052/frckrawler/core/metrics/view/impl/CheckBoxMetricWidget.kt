package com.team2052.frckrawler.core.metrics.view.impl

import android.content.Context
import android.support.v7.widget.AppCompatCheckBox
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.team2052.frckrawler.core.metrics.MetricDataHelper
import com.team2052.frckrawler.core.metrics.R
import com.team2052.frckrawler.core.metrics.data.MetricValue
import com.team2052.frckrawler.core.metrics.view.ListIndexMetricWidget
import java.util.*

class CheckBoxMetricWidget : ListIndexMetricWidget {
    private var values: LinearLayout? = null
    private var name: TextView? = null

    @Suppress("unused")
    constructor(context: Context, m: MetricValue) : super(context, m) {
        val optionalValues = MetricDataHelper.getListItemIndexRange(m.metric)
        if (!optionalValues.isPresent)
            throw IllegalStateException("Couldn't parse range values, cannot proceed")
        val rangeValues = optionalValues.get()

        for (i in rangeValues.indices) {
            val value = rangeValues[i]
            val checkbox = AppCompatCheckBox(getContext())
            checkbox.text = value
            values!!.addView(checkbox)
        }

        setMetricValue(m)
    }

    constructor(context: Context) : super(context) {}

    override fun initViews() {
        inflater.inflate(R.layout.widget_metric_checkbox, this)
        name = findViewById<View>(R.id.name) as TextView
        values = findViewById<View>(R.id.values) as LinearLayout
    }

    override fun setMetricValue(m: MetricValue) {
        name!!.text = m.metric.name

        val preLoadedValuesResult = MetricDataHelper.getListIndexMetricValue(m)

        for (i in 0..values!!.childCount - 1) {
            (values!!.getChildAt(i) as AppCompatCheckBox).isChecked = preLoadedValuesResult.t1.contains(i)
        }
    }

    override fun getIndexValues(): List<Int> {
        val index_values = ArrayList<Int>()

        for (i in 0..this.values!!.childCount - 1) {
            val check_box = this.values!!.getChildAt(i) as AppCompatCheckBox
            if (check_box.isChecked) {
                index_values.add(i)
            }
        }

        return index_values
    }
}
