package com.team2052.frckrawler.core.metrics.types;

import com.team2052.frckrawler.core.common.MetricHelper;
import com.team2052.frckrawler.core.metrics.NumberMetricType;
import com.team2052.frckrawler.core.metrics.view.impl.StopwatchMetricWidget;

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
