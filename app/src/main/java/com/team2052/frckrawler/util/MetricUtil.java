package com.team2052.frckrawler.util;

/**
 * Created by adam on 3/28/15.
 */

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.team2052.frckrawler.db.Game;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.tba.JSON;

import java.util.List;

/**
 * @author Adam
 * @since 12/21/2014.
 */
public class MetricUtil {
    public static final int BOOLEAN = 0;
    public static final int COUNTER = 1;
    public static final int SLIDER = 2;
    public static final int CHOOSER = 3;
    public static final int CHECK_BOX = 4;

    public static final int MATCH_PERF_METRICS = 0;
    public static final int ROBOT_METRICS = 1;

    public static Metric createBooleanMetric(Game game, MetricType metricCategory, String name, String description) {
        JsonObject range = new JsonObject();
        range.addProperty("description", description);
        return new Metric(
                null,
                name,
                metricCategory.ordinal(),
                BOOLEAN,
                JSON.getGson().toJson(range),
                game.getId());
    }

    public static Metric createCounterMetric(Game game, MetricType metricCategory, String name, String description, int min, int max, int incrementation) {
        JsonObject data = new JsonObject();
        data.addProperty("description", description);
        data.addProperty("min", min);
        data.addProperty("max", max);
        data.addProperty("inc", incrementation);

        return new Metric(
                null,
                name,
                metricCategory.ordinal(),
                COUNTER,
                JSON.getGson().toJson(data),
                game.getId());
    }

    public static Metric createSliderMetric(Game game, MetricType metricCategory, String name, String description, int min, int max) {
        JsonObject data = new JsonObject();
        data.addProperty("description", description);
        data.addProperty("min", min);
        data.addProperty("max", max);

        return new Metric(null, name, metricCategory.ordinal(), SLIDER, JSON.getGson().toJson(data), game.getId());
    }

    public static Metric createChooserMetric(Game game, MetricType metricCategory, String name, String description, List<String> choices) {
        JsonObject data = new JsonObject();
        JsonElement jsonElements = JSON.getGson().toJsonTree(choices);
        data.addProperty("description", description);
        data.add("values", jsonElements);

        String str_data = JSON.getGson().toJson(data);

        return new Metric(null, name, metricCategory.ordinal(), CHOOSER, str_data, game.getId());
    }

    public static Metric createCheckBoxMetric(Game game, MetricType metricCategory, String name, String description, List<String> values) {
        JsonObject data = new JsonObject();
        JsonElement jsonElements = JSON.getGson().toJsonTree(values);
        data.addProperty("description", description);
        data.add("values", jsonElements);

        String str_data = JSON.getGson().toJson(data);


        return new Metric(null,
                name,
                metricCategory.ordinal(),
                CHECK_BOX,
                str_data,
                game.getId());
    }

    @Deprecated
    public enum MetricType {
        MATCH_PERF_METRICS, ROBOT_METRICS;
        public static final MetricType[] VALID_TYPES = values();
    }


}