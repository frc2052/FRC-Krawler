package com.team2052.frckrawler.core.metrics.types;

import com.team2052.frckrawler.core.common.MetricHelper;
import com.team2052.frckrawler.core.metrics.IndexValueMetricType;
import com.team2052.frckrawler.core.metrics.view.impl.CheckBoxMetricWidget;

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
