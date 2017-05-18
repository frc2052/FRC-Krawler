package com.team2052.frckrawler.metric.types;

import com.team2052.frckrawler.helpers.metric.MetricHelper;
import com.team2052.frckrawler.metric.NumberMetricType;
import com.team2052.frckrawler.metric.view.impl.StopwatchMetricWidget;

public class StopWatchMetricType extends NumberMetricType<StopwatchMetricWidget> {
    @Override
    public Class<StopwatchMetricWidget> widgetClass() {
        return StopwatchMetricWidget.class;
    }

    @Override
    public int getType() {
        return MetricHelper.STOP_WATCH;
    }
}
