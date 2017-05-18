package com.team2052.frckrawler.metric.data;

import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.JsonElement;
import com.team2052.frckrawler.helpers.metric.MetricHelper;
import com.team2052.frckrawler.models.Metric;

@AutoValue
public abstract class MetricValue {
    public static MetricValue create(Metric metric, JsonElement value) {
        return new AutoValue_MetricValue(metric, value);
    }

    public abstract Metric metric();

    @Nullable
    public abstract JsonElement value();

    public String valueAsString() {
        return value() == null ? "" : MetricHelper.convertToJsonString(value());
    }

    @MetricHelper.MetricType
    public int getMetricType() {
        return metric().getType();
    }
}
