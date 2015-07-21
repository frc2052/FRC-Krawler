package com.team2052.frckrawler.database;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.team2052.frckrawler.tba.JSON;
import com.team2052.frckrawler.util.MetricUtil;
import com.team2052.frckrawler.util.Tuple2;

import java.util.List;

public class MetricHelper {
    public static Optional<JsonElement> getMetricValue(MetricValue metricValue) {
        if (metricValue == null)
            return Optional.absent();
        if (metricValue.getValue() == null)
            return Optional.absent();
        return Optional.of(JSON.getParser().parse(metricValue.getValue()));
    }

    public static Tuple2<Boolean, CompileResult> compileBooleanMetricValue(MetricValue metricValue) {
        if (metricValue.getMetric().getType() != MetricUtil.BOOLEAN)
            return new Tuple2<>(false, CompileResult.WRONG_METRIC_TYPE);

        Optional<JsonElement> optional = getMetricValue(metricValue);
        if (!optional.isPresent())
            return new Tuple2<>(false, CompileResult.ABSENT_VALUE);

        JsonObject value = optional.get().getAsJsonObject();
        if (value.has("value") && !value.get("value").isJsonNull()) {
            try {
                return new Tuple2<>(value.get("value").getAsBoolean(), CompileResult.SUCCEED);
            } catch (ClassCastException e) {
                return new Tuple2<>(false, CompileResult.WRONG_TYPE_VALUE);
            }
        }
        return new Tuple2<>(false, CompileResult.OTHER_ERROR);
    }

    public static Tuple2<Integer, CompileResult> getIntMetricValue(MetricValue metricValue) {
        if (metricValue.getMetric().getType() != MetricUtil.SLIDER && metricValue.getMetric().getType() != MetricUtil.COUNTER)
            return new Tuple2<>(-1, CompileResult.WRONG_METRIC_TYPE);

        final Optional<JsonElement> optional = getMetricValue(metricValue);
        if (!optional.isPresent())
            return new Tuple2<>(-1, CompileResult.ABSENT_VALUE);

        JsonObject value = optional.get().getAsJsonObject();
        if (value.has("value") && !value.get("value").isJsonNull()) {
            try {
                return new Tuple2<>(value.get("value").getAsInt(), CompileResult.SUCCEED);
            } catch (ClassCastException e) {
                return new Tuple2<>(-1, CompileResult.WRONG_TYPE_VALUE);
            }
        }
        return new Tuple2<>(-1, CompileResult.OTHER_ERROR);
    }

    //NOT IMPLEMENTED
    public static Tuple2<List<Integer>, CompileResult> getListIndexMetricValue(MetricValue metricValue) {
        return new Tuple2<>(Lists.newArrayList(), CompileResult.OTHER_ERROR);
    }

    public static JsonObject buildBooleanMetricValue(boolean value) {
        JsonObject json = new JsonObject();
        json.addProperty("value", value);
        return json;
    }

    public static Optional<Boolean> getBooleanValue(MetricValue metricValue) {
        final Optional<JsonElement> optionalValue = getMetricValue(metricValue);
        if (!optionalValue.isPresent())
            return Optional.absent();
        final JsonObject value = optionalValue.get().getAsJsonObject();
        return getBooleanValue(value);
    }

    public static Optional<Boolean> getBooleanValue(JsonObject json) {
        if (json == null)
            return Optional.absent();

        if (json.has("value") && !json.get("value").isJsonNull()) {
            try {
                return Optional.of(json.get("value").getAsBoolean());
            } catch (ClassCastException e) {
                return Optional.absent();
            }
        }
        return Optional.absent();
    }


    public static String convertToJsonString(JsonElement json) {
        return JSON.getGson().toJson(json);
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
