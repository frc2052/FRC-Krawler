package com.team2052.frckrawler.metric.types;

import com.team2052.frckrawler.helpers.metric.MetricHelper;
import com.team2052.frckrawler.metric.NumberMetricType;
import com.team2052.frckrawler.metric.view.impl.SliderMetricWidget;

public class SliderMetricType extends NumberMetricType<SliderMetricWidget> {
    @Override
    protected Class<SliderMetricWidget> widgetClass() {
        return SliderMetricWidget.class;
    }

    @Override
    public int getType() {
        return MetricHelper.SLIDER;
    }
}
