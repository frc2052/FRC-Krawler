package com.team2052.frckrawler.core.metrics.types;

import com.team2052.frckrawler.core.common.MetricHelper;
import com.team2052.frckrawler.core.metrics.NumberMetricType;
import com.team2052.frckrawler.core.metrics.view.impl.CounterMetricWidget;

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
