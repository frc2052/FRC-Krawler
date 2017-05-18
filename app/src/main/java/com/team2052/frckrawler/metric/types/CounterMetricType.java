package com.team2052.frckrawler.metric.types;

import com.team2052.frckrawler.helpers.metric.MetricHelper;
import com.team2052.frckrawler.metric.NumberMetricType;
import com.team2052.frckrawler.metric.view.impl.CounterMetricWidget;

public class CounterMetricType extends NumberMetricType<CounterMetricWidget> {
    @Override
    protected Class<CounterMetricWidget> widgetClass() {
        return CounterMetricWidget.class;
    }

    @Override
    public int getType() {
        return MetricHelper.COUNTER;
    }
}
