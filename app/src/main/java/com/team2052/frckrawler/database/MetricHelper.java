package com.team2052.frckrawler.database;

import com.google.common.base.Optional;
import com.google.gson.JsonObject;
import com.team2052.frckrawler.tba.JSON;
import com.team2052.frckrawler.util.MetricUtil;
import com.team2052.frckrawler.util.Tuple2;

public class MetricHelper {
    public static Optional<JsonObject> getMetricValue(MetricValue metricValue) {
        if (metricValue == null)
            return Optional.absent();
        if (metricValue.getValue() == null)
            return Optional.absent();
        return Optional.of(JSON.getAsJsonObject(metricValue.getValue()));
    }

    public static Tuple2<Boolean, CompileResult> compileBooleanMetricValue(MetricValue metricValue) {
        if (metricValue.getMetric().getType() != MetricUtil.BOOLEAN)
            return new Tuple2<>(false, CompileResult.WRONG_METRIC_TYPE);

        Optional<JsonObject> optional = getMetricValue(metricValue);
        if (!optional.isPresent())
            return new Tuple2<>(false, CompileResult.ABSENT_VALUE);

        JsonObject value = optional.get();

        if (value.has("value") && !value.get("value").isJsonNull()) {
            try {
                return new Tuple2<>(value.get("value").getAsBoolean(), CompileResult.SUCCEED);
            } catch (ClassCastException e) {
                return new Tuple2<>(false, CompileResult.WRONG_TYPE_VALUE);
            }
        }
        return new Tuple2<>(false, CompileResult.OTHER_ERROR);
    }

    public enum CompileResult {
        SUCCEED(false),
        ABSENT_VALUE(true),
        WRONG_METRIC_TYPE(true),
        WRONG_TYPE_VALUE(true),
        OTHER_ERROR(true);

        public boolean isError;

        CompileResult(boolean isError) {
            this.isError = isError;
        }
    }


}
