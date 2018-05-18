package com.team2052.frckrawler.core.metrics.types;

import com.team2052.frckrawler.core.common.MetricHelper;
import com.team2052.frckrawler.core.metrics.NumberMetricType;
import com.team2052.frckrawler.core.metrics.view.impl.SliderMetricWidget;

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
