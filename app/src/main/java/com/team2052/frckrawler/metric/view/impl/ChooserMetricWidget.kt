package com.team2052.frckrawler.metric.view.impl

import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import com.google.common.collect.Lists
import com.team2052.frckrawler.R
import com.team2052.frckrawler.helpers.metric.MetricDataHelper
import com.team2052.frckrawler.metric.data.MetricValue
import com.team2052.frckrawler.metric.view.ListIndexMetricWidget

class ChooserMetricWidget : ListIndexMetricWidget, OnItemSelectedListener {
    internal var value: Int = 0
    private var chooserSpinner: Spinner? = null

    @Suppress("unused")
    constructor(context: Context, metricValue: MetricValue) : super(context, metricValue) {
        setMetricValue(metricValue)
    }

    constructor(context: Context) : super(context) {}

    override fun setMetricValue(m: MetricValue) {
        (findViewById<View>(R.id.name) as TextView).text = m.metric.name
        val optionalValues = MetricDataHelper.getListItemIndexRange(m.metric)
        if (!optionalValues.isPresent)
            throw IllegalStateException("Couldn't parse values, cannot proceed")

        val adapter = ArrayAdapter<Any>(context, android.R.layout.simple_list_item_1)
        for (value in optionalValues.get()) adapter.add(value)
        chooserSpinner!!.adapter = adapter

        var selectedPos = 0
        val preloadedValuesResult = MetricDataHelper.getListIndexMetricValue(m)
        if (!preloadedValuesResult.t2.isError)
            if (!preloadedValuesResult.t1.isEmpty())
                selectedPos = preloadedValuesResult.t1[0]

        if (!adapter.isEmpty)
            chooserSpinner!!.setSelection(selectedPos)
    }

    override fun initViews() {
        inflater.inflate(R.layout.widget_metric_chooser, this)
        chooserSpinner = findViewById<View>(R.id.choooserList) as Spinner
        chooserSpinner?.onItemSelectedListener = this
    }

    override fun onItemSelected(a: AdapterView<*>, arg1: View?, pos: Int, arg3: Long) {
        value = pos
    }

    override fun onNothingSelected(a: AdapterView<*>) {
        a.setSelection(0)
    }

    override fun getIndexValues(): List<Int> {
        return Lists.newArrayList(value)
    }
}
