package com.team2052.frckrawler.adapters.items.smart.metric;

import android.support.annotation.NonNull;

import com.team2052.frckrawler.helpers.metric.MetricHelper;
import com.team2052.frckrawler.metric.data.CompiledMetricValue;

import io.nlopez.smartadapters.builders.DefaultBindableLayoutBuilder;
import io.nlopez.smartadapters.utils.Mapper;
import io.nlopez.smartadapters.views.BindableLayout;

/**
 * Created by Adam on 5/1/2017.
 */

public class MetricSummaryCardMultiMapper extends DefaultBindableLayoutBuilder {
    @Override
    public Class<? extends BindableLayout> viewType(@NonNull Object item, int position, @NonNull Mapper mapper) {
        if (item instanceof CompiledMetricValue) {
            CompiledMetricValue compiledMetricValue = (CompiledMetricValue) item;
            switch (compiledMetricValue.getMetric().getType()) {
                case MetricHelper.BOOLEAN:
                    return BooleanMetricValueSummaryCard.class;
                case MetricHelper.COUNTER:
                case MetricHelper.STOP_WATCH:
                case MetricHelper.SLIDER:
                    return NumberMetricSummaryCard.class;
                case MetricHelper.CHOOSER:
                case MetricHelper.CHECK_BOX:
                    return IndexValueSummaryCard.class;
            }
        }
        return super.viewType(item, position, mapper);
    }

    @Override
    public boolean allowsMultimapping() {
        return true;
    }
}
