package com.team2052.frckrawler.metric.types;

import com.team2052.frckrawler.helpers.metric.MetricHelper;
import com.team2052.frckrawler.metric.IndexValueMetricType;
import com.team2052.frckrawler.metric.view.impl.CheckBoxMetricWidget;

public class CheckBoxMetricType extends IndexValueMetricType<CheckBoxMetricWidget> {
    @Override
    protected Class<CheckBoxMetricWidget> widgetClass() {
        return CheckBoxMetricWidget.class;
    }

    @Override
    public int getType() {
        return MetricHelper.CHECK_BOX;
    }
}
