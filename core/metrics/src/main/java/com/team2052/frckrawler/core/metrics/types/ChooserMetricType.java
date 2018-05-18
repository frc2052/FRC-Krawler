package com.team2052.frckrawler.core.metrics.types;

import com.team2052.frckrawler.core.common.MetricHelper;
import com.team2052.frckrawler.core.metrics.IndexValueMetricType;
import com.team2052.frckrawler.core.metrics.view.impl.ChooserMetricWidget;

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
