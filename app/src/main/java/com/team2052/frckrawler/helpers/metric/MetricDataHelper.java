package com.team2052.frckrawler.helpers.metric;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.team2052.frckrawler.data.tba.JSON;
import com.team2052.frckrawler.helpers.Tuple2;
import com.team2052.frckrawler.metric.data.MetricValue;
import com.team2052.frckrawler.models.MatchDatum;
import com.team2052.frckrawler.models.Metric;
import com.team2052.frckrawler.models.PitDatum;

import java.lang.reflect.Type;
import java.util.List;

import rx.functions.Func1;

public class MetricDataHelper {
    public static Func1<PitDatum, MetricValue> mapPitDataToMetricValue =
            pitData -> MetricValue.create(pitData.getMetric(), JSON.getAsJsonObject(pitData.getData()));
    public static Func1<MatchDatum, MetricValue> mapMatchDataToMetricValue = matchDatum -> {
        JsonObject value = JSON.getAsJsonObject(matchDatum.getData());
        //Add match number for compiling purposes
        value.addProperty("match_number", matchDatum.getMatch_number());
        return MetricValue.create(matchDatum.getMetric(), value);
    };

    private static Type listType = new TypeToken<List<Integer>>() {
    }.getType();

    private static Optional<JsonElement> getMetricValue(MetricValue metricValue) {
        if (metricValue == null)
            return Optional.absent();
        if (metricValue.value() == null)
            return Optional.absent();
        return Optional.of(metricValue.value());
    }

    private static Optional<JsonElement> getMetricData(Metric metric) {
        if (metric == null)
            return Optional.absent();
        if (metric.getData() == null)
            return Optional.absent();
        return Optional.of(JSON.getAsJsonObject(metric.getData()));
    }

    public static Tuple2<Double, ReturnResult> getDoubleMetricValue(MetricValue metricValue) {
        if (metricValue.getMetricType() != MetricHelper.STOP_WATCH
                && metricValue.getMetricType() != MetricHelper.COUNTER
                && metricValue.getMetricType() != MetricHelper.SLIDER)
            return new Tuple2<>(-1.0, ReturnResult.WRONG_METRIC_TYPE);

        final Optional<JsonElement> optional = getMetricValue(metricValue);
        if (!optional.isPresent())
            return new Tuple2<>(-1.0, ReturnResult.ABSENT_VALUE);

        JsonObject value = optional.get().getAsJsonObject();
        if (value.has("value") && !value.get("value").isJsonNull()) {
            try {
                return new Tuple2<>(value.get("value").getAsDouble(), ReturnResult.SUCCEED);
            } catch (ClassCastException e) {
                return new Tuple2<>(-1.0, ReturnResult.WRONG_TYPE_VALUE);
            }
        }
        return new Tuple2<>(-1.0, ReturnResult.OTHER_ERROR);
    }

    public static Tuple2<List<Integer>, ReturnResult> getListIndexMetricValue(MetricValue metricValue) {
        if (metricValue.getMetricType() != MetricHelper.CHECK_BOX && metricValue.getMetricType() != MetricHelper.CHOOSER)
            return new Tuple2<>(Lists.newArrayList(), ReturnResult.WRONG_METRIC_TYPE);

        final Optional<JsonElement> optionalValue = getMetricValue(metricValue);
        if (!optionalValue.isPresent())
            return new Tuple2<>(Lists.newArrayList(), ReturnResult.ABSENT_VALUE);

        List<Integer> array = null;
        final JsonObject valueJson = optionalValue.get().getAsJsonObject();
        if (valueJson.has("values") && !valueJson.get("values").isJsonNull()) {
            array = JSON.getGson().fromJson(valueJson.get("values"), listType);
            return new Tuple2<>(array, ReturnResult.SUCCEED);
        }
        return new Tuple2<>(Lists.newArrayList(), ReturnResult.OTHER_ERROR);
    }

    public static Tuple2<String, ReturnResult> getStringMetricValue(MetricValue metricValue) {
        if (metricValue.getMetricType() != MetricHelper.TEXT_FIELD)
            return new Tuple2<>(null, ReturnResult.WRONG_METRIC_TYPE);

        final Optional<JsonElement> optionalValue = getMetricValue(metricValue);
        if (!optionalValue.isPresent())
            return new Tuple2<>(null, ReturnResult.ABSENT_VALUE);

        List<Integer> array = null;
        final JsonObject valueJson = optionalValue.get().getAsJsonObject();
        if (valueJson.has("value") && !valueJson.get("value").isJsonNull()) {
            String text = valueJson.get("value").getAsString();
            return new Tuple2<>(text, ReturnResult.SUCCEED);
        }
        return new Tuple2<>(null, ReturnResult.OTHER_ERROR);
    }

    public static Tuple2<Integer, MetricDataHelper.ReturnResult> getIntMetricValue(MetricValue metricValue) {
        if (metricValue.getMetricType() != MetricHelper.SLIDER && metricValue.getMetricType() != MetricHelper.COUNTER)
            return new Tuple2<>(-1, MetricDataHelper.ReturnResult.WRONG_METRIC_TYPE);

        final Optional<JsonElement> optional = getMetricValue(metricValue);
        if (!optional.isPresent())
            return new Tuple2<>(-1, MetricDataHelper.ReturnResult.ABSENT_VALUE);

        JsonObject value = optional.get().getAsJsonObject();
        if (value.has("value") && !value.get("value").isJsonNull()) {
            try {
                return new Tuple2<>(value.get("value").getAsInt(), MetricDataHelper.ReturnResult.SUCCEED);
            } catch (ClassCastException e) {
                return new Tuple2<>(-1, MetricDataHelper.ReturnResult.WRONG_TYPE_VALUE);
            }
        }
        return new Tuple2<>(-1, MetricDataHelper.ReturnResult.OTHER_ERROR);
    }

    public static Tuple2<Integer, MetricDataHelper.ReturnResult> getMatchNumberFromMetricValue(MetricValue metricValue) {
        final Optional<JsonElement> optional = getMetricValue(metricValue);
        if (!optional.isPresent())
            return new Tuple2<>(-1, MetricDataHelper.ReturnResult.ABSENT_VALUE);

        JsonObject value = optional.get().getAsJsonObject();
        if (value.has("match_number") && !value.get("match_number").isJsonNull()) {
            try {
                return new Tuple2<>(value.get("match_number").getAsInt(), MetricDataHelper.ReturnResult.SUCCEED);
            } catch (ClassCastException e) {
                return new Tuple2<>(-1, MetricDataHelper.ReturnResult.WRONG_TYPE_VALUE);
            }
        }
        return new Tuple2<>(-1, MetricDataHelper.ReturnResult.OTHER_ERROR);
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

    public static Optional<List<String>> getListItemIndexRange(Metric metric) {
        if (metric.getType() != MetricHelper.CHECK_BOX && metric.getType() != MetricHelper.CHOOSER)
            return Optional.absent();

        final Optional<JsonElement> optionalData = getMetricData(metric);
        if (!optionalData.isPresent())
            return Optional.absent();

        final JsonObject dataJson = optionalData.get().getAsJsonObject();
        if (dataJson.has("values") && !dataJson.get("values").isJsonNull() && dataJson.get("values").isJsonArray()) {
            final JsonArray values = dataJson.get("values").getAsJsonArray();
            final List<String> range = Lists.newArrayList();
            for (JsonElement value : values) range.add(value.getAsString());
            return Optional.of(range);
        }
        return Optional.absent();
    }

    public static JsonObject buildBooleanMetricValue(boolean value) {
        JsonObject json = new JsonObject();
        json.addProperty("value", value);
        return json;
    }

    public static JsonObject buildStringMetricValue(String value) {
        JsonObject json = new JsonObject();
        json.addProperty("value", value);
        return json;
    }

    public static Tuple2<Boolean, ReturnResult> getBooleanMetricValue(MetricValue metricValue) {
        if (metricValue.getMetricType() != MetricHelper.BOOLEAN)
            return new Tuple2<>(false, ReturnResult.WRONG_METRIC_TYPE);

        Optional<JsonElement> optional = getMetricValue(metricValue);
        if (!optional.isPresent())
            return new Tuple2<>(false, ReturnResult.ABSENT_VALUE);

        JsonObject value = optional.get().getAsJsonObject();
        if (value.has("value") && !value.get("value").isJsonNull()) {
            try {
                return new Tuple2<>(value.get("value").getAsBoolean(), ReturnResult.SUCCEED);
            } catch (ClassCastException e) {
                return new Tuple2<>(false, ReturnResult.WRONG_TYPE_VALUE);
            }
        }
        return new Tuple2<>(false, ReturnResult.OTHER_ERROR);
    }

    public static JsonObject buildNumberMetricValue(Number value) {
        JsonObject data = new JsonObject();
        data.addProperty("value", value);
        return data;
    }

    public static JsonObject buildListIndexValue(List<Integer> index_data) {
        JsonObject data = new JsonObject();
        JsonElement values = JSON.getGson().toJsonTree(index_data);
        data.add("values", values);
        return data;
    }

    /**
     * Get the weight for the current data
     */
    public static double getCompileWeightForMatchNumber(MetricValue metricValue, List<MetricValue> metricData, double compileWeight) {
        return Math.pow(compileWeight, metricData.indexOf(metricValue) + 1);
    }

    public enum ReturnResult {
        SUCCEED(false),
        ABSENT_VALUE(true),
        WRONG_METRIC_TYPE(true),
        WRONG_TYPE_VALUE(true),
        OTHER_ERROR(true);

        public boolean isError;

        ReturnResult(boolean isError) {
            this.isError = isError;
        }
    }
}
