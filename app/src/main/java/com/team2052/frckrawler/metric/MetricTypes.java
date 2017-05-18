package com.team2052.frckrawler.metric;

import android.content.Context;
import android.support.annotation.NonNull;

import com.team2052.frckrawler.helpers.metric.MetricHelper;
import com.team2052.frckrawler.metric.data.MetricValue;
import com.team2052.frckrawler.metric.types.BooleanMetricType;
import com.team2052.frckrawler.metric.types.CheckBoxMetricType;
import com.team2052.frckrawler.metric.types.ChooserMetricType;
import com.team2052.frckrawler.metric.types.CounterMetricType;
import com.team2052.frckrawler.metric.types.SliderMetricType;
import com.team2052.frckrawler.metric.types.StopWatchMetricType;
import com.team2052.frckrawler.metric.view.MetricWidget;
import com.team2052.frckrawler.models.Metric;

public class MetricTypes {
    private static BooleanMetricType booleanMetricType = new BooleanMetricType();
    private static NumberMetricType counterMetricType = new CounterMetricType();
    private static NumberMetricType sliderMetricType = new SliderMetricType();
    private static ChooserMetricType chooserMetricType = new ChooserMetricType();
    private static CheckBoxMetricType checkBoxMetricType = new CheckBoxMetricType();
    private static StopWatchMetricType stopWatchMetricType = new StopWatchMetricType();

    public static MetricWidget createWidget(Context context, @MetricHelper.MetricType int metric_type) {
        return getType(metric_type).createWidget(context);
    }

    public static MetricWidget createWidget(Context context, @NonNull Metric metric) {
        return createWidget(context, MetricValue.create(metric, null));
    }

    public static MetricWidget createWidget(Context context, @NonNull MetricValue metricValue) {
        return getType(metricValue.metric().getType()).createWidget(context, metricValue);
    }

    /**
     * Get's the corresponding type value for metric
     * Throws exception if metric type isn't handled or doesn't exist
     */
    @NonNull
    public static MetricType getType(@MetricHelper.MetricType int type) {
        switch (type) {
            case MetricHelper.BOOLEAN:
                return booleanMetricType;
            case MetricHelper.COUNTER:
                return counterMetricType;
            case MetricHelper.SLIDER:
                return sliderMetricType;
            case MetricHelper.CHOOSER:
                return chooserMetricType;
            case MetricHelper.CHECK_BOX:
                return checkBoxMetricType;
            case MetricHelper.STOP_WATCH:
                return stopWatchMetricType;
            case MetricHelper.TEXT_FIELD:
            default:
                throw new IllegalStateException("Metric type not found or handled");
        }
    }
}
