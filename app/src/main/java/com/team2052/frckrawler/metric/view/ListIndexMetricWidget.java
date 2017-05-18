package com.team2052.frckrawler.metric.view;

import android.content.Context;

import com.google.gson.JsonElement;
import com.team2052.frckrawler.helpers.metric.MetricDataHelper;
import com.team2052.frckrawler.metric.data.MetricValue;

import java.util.List;

public abstract class ListIndexMetricWidget extends MetricWidget {
    protected ListIndexMetricWidget(Context context, MetricValue m) {
        super(context, m);
    }

    public ListIndexMetricWidget(Context context) {
        super(context);
    }

    /**
     * @return the values that are valid for compiling ex if a checkbox is checked the index value of that checkbox should be in the list.
     */
    public abstract List<Integer> getIndexValues();

    @Override
    public JsonElement getData() {
        return MetricDataHelper.buildListIndexValue(getIndexValues());
    }
}
