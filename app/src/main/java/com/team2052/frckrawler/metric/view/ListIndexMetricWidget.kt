package com.team2052.frckrawler.metric.view

import android.content.Context

import com.google.gson.JsonElement
import com.team2052.frckrawler.helpers.metric.MetricDataHelper
import com.team2052.frckrawler.metric.data.MetricValue

abstract class ListIndexMetricWidget : MetricWidget {
    protected constructor(context: Context, m: MetricValue) : super(context, m)

    constructor(context: Context) : super(context)

    /**
     * @return the values that are valid for compiling ex if a checkbox is checked the index value of that checkbox should be in the list.
     */
    abstract fun getIndexValues(): List<Int>

    override val data: JsonElement get() = MetricDataHelper.buildListIndexValue(getIndexValues())
}
