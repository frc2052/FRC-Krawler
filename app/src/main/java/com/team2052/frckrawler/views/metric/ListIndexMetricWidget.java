package com.team2052.frckrawler.views.metric;

import android.content.Context;

import com.google.gson.JsonElement;
import com.team2052.frckrawler.database.metric.MetricHelper;
import com.team2052.frckrawler.database.metric.MetricValue;

import java.util.List;

/**
 * Created by Adam Corpstein on 7/27/2015.
 */
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
        return MetricHelper.buildListIndexValue(getIndexValues());
    }
}
