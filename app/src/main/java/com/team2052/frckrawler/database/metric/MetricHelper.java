package com.team2052.frckrawler.database.metric;

import android.support.annotation.IntDef;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.team2052.frckrawler.db.Game;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.tba.JSON;
import com.team2052.frckrawler.util.Tuple2;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Type;
import java.util.List;

public class MetricHelper {
    public static final int MATCH_PERF_METRICS = 0, ROBOT_METRICS = 1;
    public static final int BOOLEAN = 0, COUNTER = 1, SLIDER = 2, CHOOSER = 3, CHECK_BOX = 4;

    private static Type listType = new TypeToken<List<Integer>>() {
    }.getType();

    public static Optional<JsonElement> getMetricValue(MetricValue metricValue) {
        if (metricValue == null)
            return Optional.absent();
        if (metricValue.getValue() == null)
            return Optional.absent();
        return Optional.of(metricValue.getValue());
    }

    public static Optional<JsonElement> getMetricData(Metric metric) {
        if (metric == null)
            return Optional.absent();
        if (metric.getData() == null)
            return Optional.absent();
        return Optional.of(JSON.getAsJsonObject(metric.getData()));
    }

    public static Tuple2<Boolean, ReturnResult> compileBooleanMetricValue(MetricValue metricValue) {
        if (metricValue.getMetric().getType() != BOOLEAN)
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

    public static Tuple2<Integer, ReturnResult> getIntMetricValue(MetricValue metricValue) {
        if (metricValue.getMetric().getType() != SLIDER && metricValue.getMetric().getType() != COUNTER)
            return new Tuple2<>(-1, ReturnResult.WRONG_METRIC_TYPE);

        final Optional<JsonElement> optional = getMetricValue(metricValue);
        if (!optional.isPresent())
            return new Tuple2<>(-1, ReturnResult.ABSENT_VALUE);

        JsonObject value = optional.get().getAsJsonObject();
        if (value.has("value") && !value.get("value").isJsonNull()) {
            try {
                return new Tuple2<>(value.get("value").getAsInt(), ReturnResult.SUCCEED);
            } catch (ClassCastException e) {
                return new Tuple2<>(-1, ReturnResult.WRONG_TYPE_VALUE);
            }
        }
        return new Tuple2<>(-1, ReturnResult.OTHER_ERROR);
    }

    public static Tuple2<List<Integer>, ReturnResult> getListIndexMetricValue(MetricValue metricValue) {
        if (metricValue.getMetric().getType() != CHECK_BOX && metricValue.getMetric().getType() != CHOOSER)
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

    public static JsonObject buildBooleanMetricValue(boolean value) {
        JsonObject json = new JsonObject();
        json.addProperty("value", value);
        return json;
    }

    public static JsonObject buildIntMetricValue(int value) {
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
        if (metric.getType() != CHECK_BOX && metric.getType() != CHOOSER)
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

    public static String convertToJsonString(JsonElement json) {
        return JSON.getGson().toJson(json);
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

    @IntDef({BOOLEAN, COUNTER, SLIDER, CHOOSER, CHECK_BOX})
    @Retention(RetentionPolicy.SOURCE)
    public @interface MetricType {
    }

    @IntDef({MATCH_PERF_METRICS, ROBOT_METRICS})
    @Retention(RetentionPolicy.SOURCE)
    public @interface MetricCategory {
    }

    public static class MetricFactory {
        final Game game;
        @MetricCategory
        int metricCategory;
        @MetricType
        int metricType;
        String name;
        JsonObject data = new JsonObject();

        public MetricFactory(Game game, String name) {
            if (game == null || name.isEmpty())
                throw new IllegalStateException("Couldn't create MetricFactory");
            this.game = game;
            this.name = name;
        }

        public void setMetricType(@MetricType int metricType) {
            this.metricType = metricType;
        }

        public void setMetricCategory(@MetricCategory int metricCategory) {
            this.metricCategory = metricCategory;
        }

        public void setDataListIndexValue(List<String> names) {
            JsonElement jsonElements = JSON.getGson().toJsonTree(names);
            data.add("values", jsonElements);
        }

        public void setDataMinMaxInc(int min, int max, Integer inc) {
            data.addProperty("min", min);
            data.addProperty("max", max);
            if (inc != null)
                data.addProperty("inc", inc);
        }

        public void setDescription(String description) {
            data.addProperty("description", Strings.nullToEmpty(description));
        }

        private void clean() {
            //Clean up data if needed
            if (!data.has("description"))
                data.addProperty("description", "");

            switch (metricType) {
                case BOOLEAN:
                    if (data.has("values"))
                        data.remove("values");
                    if (data.has("min"))
                        data.remove("min");
                    if (data.has("max"))
                        data.remove("max");
                    if (data.has("inc"))
                        data.remove("inc");
                    break;
                case SLIDER:
                case COUNTER:
                    if (data.has("values"))
                        data.remove("values");
                    break;
                case CHOOSER:
                case CHECK_BOX:
                    if (data.has("min"))
                        data.remove("min");
                    if (data.has("max"))
                        data.remove("max");
                    if (data.has("inc"))
                        data.remove("inc");
                    break;
            }
        }

        public Metric buildMetric() {
            clean();
            return new Metric(
                    null,
                    name,
                    metricCategory,
                    metricType,
                    JSON.getGson().toJson(data),
                    game.getId(),
                    true);
        }
    }
}
