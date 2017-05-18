package com.team2052.frckrawler.metric;

import android.content.Context;
import android.support.annotation.Nullable;

import com.google.gson.JsonObject;
import com.team2052.frckrawler.helpers.metric.MetricHelper;
import com.team2052.frckrawler.metric.data.CompiledMetricValue;
import com.team2052.frckrawler.metric.data.MetricValue;
import com.team2052.frckrawler.metric.view.MetricWidget;
import com.team2052.frckrawler.models.Metric;

import java.text.DecimalFormat;
import java.util.List;

public abstract class MetricType<W extends MetricWidget> {
    public static final DecimalFormat format = new DecimalFormat("0.00");

    protected abstract Class<W> widgetClass();

    @MetricHelper.MetricType
    public abstract int getType();

    @Nullable
    public W createWidget(Context context, MetricValue metricValue) {
        try {
            return widgetClass().getDeclaredConstructor(Context.class, MetricValue.class).newInstance(context, metricValue);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    public W createWidget(Context context) {
        try {
            return widgetClass().getDeclaredConstructor(Context.class).newInstance(context);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public abstract CompiledMetricValue compile(Metric metric, List<MetricValue> metricValues, float weight);

    public abstract String convertCompiledValueToString(JsonObject jsonObject);
}
