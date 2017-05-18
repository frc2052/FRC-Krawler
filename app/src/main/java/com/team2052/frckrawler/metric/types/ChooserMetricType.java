package com.team2052.frckrawler.metric.types;

import com.team2052.frckrawler.helpers.metric.MetricHelper;
import com.team2052.frckrawler.metric.IndexValueMetricType;
import com.team2052.frckrawler.metric.view.impl.ChooserMetricWidget;

public class ChooserMetricType extends IndexValueMetricType<ChooserMetricWidget> {
    @Override
    protected Class<ChooserMetricWidget> widgetClass() {
        return ChooserMetricWidget.class;
    }

    @Override
    public int getType() {
        return MetricHelper.CHOOSER;
    }
}
