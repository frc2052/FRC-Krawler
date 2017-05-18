package com.team2052.frckrawler.metric.data;

import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.JsonObject;
import com.team2052.frckrawler.metric.MetricTypes;
import com.team2052.frckrawler.models.Metric;
import com.team2052.frckrawler.models.Robot;

import java.util.List;

@AutoValue
public abstract class CompiledMetricValue {
    public static CompiledMetricValue create(Robot robot, Metric metric, List<MetricValue> metricValues, JsonObject jsonValue) {
        return new AutoValue_CompiledMetricValue(robot, metric, metricValues, jsonValue);
    }

    public static CompiledMetricValue create(Metric metric, List<MetricValue> metricValues, JsonObject jsonValue) {
        return new AutoValue_CompiledMetricValue(null, metric, metricValues, jsonValue);
    }

    @Nullable
    public abstract Robot robot();

    public abstract Metric metric();

    public abstract List<MetricValue> metricValues();

    public abstract JsonObject jsonValue();

    public String getReadableValue() {
        return MetricTypes.getType(metric().getType()).convertCompiledValueToString(jsonValue());
    }

    public CompiledMetricValue toRobotCompiledValue(Robot robot) {
        if (robot() != null) {
            return this;
        } else {
            return create(robot, metric(), metricValues(), jsonValue());
        }
    }
}
