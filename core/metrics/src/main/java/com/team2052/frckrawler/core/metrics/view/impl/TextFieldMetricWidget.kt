package com.team2052.frckrawler.core.metrics.view.impl

import android.content.Context
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.google.gson.JsonElement
import com.team2052.frckrawler.core.metrics.MetricDataHelper
import com.team2052.frckrawler.core.metrics.R
import com.team2052.frckrawler.core.metrics.data.MetricValue
import com.team2052.frckrawler.core.metrics.view.MetricWidget

class TextFieldMetricWidget : MetricWidget {
    lateinit var editText: EditText
    lateinit var textView: TextView

    @Suppress("unused")
    constructor(context: Context, m: MetricValue) : super(context, m) {
        inflater.inflate(R.layout.widget_metric_text_field, this)
        initViews()
        setMetricValue(m)
    }

    constructor(context: Context) : super(context) {
        inflater.inflate(R.layout.widget_metric_text_field, this)
        initViews()
    }

    override fun initViews() {
        editText = findViewById<View>(R.id.text_input) as EditText
        textView = findViewById<View>(R.id.title) as TextView
    }


    override fun setMetricValue(m: MetricValue) {
        (findViewById<View>(R.id.title) as TextView).text = m.metric.name
        val stringMetricValue = MetricDataHelper.getStringMetricValue(m)

        if (stringMetricValue.t2.isError) {
            editText.setText("")
        }

        editText.setText(stringMetricValue.t1)
    }

    override val data: JsonElement get() = MetricDataHelper.buildStringMetricValue(editText.text.toString())
}
