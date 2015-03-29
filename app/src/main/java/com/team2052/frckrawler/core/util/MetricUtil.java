package com.team2052.frckrawler.core.util;

/**
 * Created by adam on 3/28/15.
 */

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.team2052.frckrawler.core.tba.JSON;
import com.team2052.frckrawler.db.Game;
import com.team2052.frckrawler.db.Metric;

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

    public static Metric createBooleanMetric(Game game, MetricType metricCategory, String name, String description) {
        return new Metric(
                null,
                name,
                metricCategory.ordinal(),
                BOOLEAN, null, game.getId());
    }

    public static Metric createCounterMetric(Game game, MetricType metricCategory, String name, String description, int min, int max, int incrementation) {
        JsonObject range = new JsonObject();
        range.addProperty("min", min);
        range.addProperty("max", max);
        range.addProperty("inc", incrementation);

        return new Metric(
                null,
                name,
                metricCategory.ordinal(),
                COUNTER,
                JSON.getGson().toJson(range),
                game.getId());
    }

    public static Metric createSliderMetric(Game game, MetricType metricCategory, String name, String description, int min, int max) {
        JsonObject range = new JsonObject();
        range.addProperty("min", min);
        range.addProperty("max", max);

        return new Metric(null, name, metricCategory.ordinal(), SLIDER, JSON.getGson().toJson(range), game.getId());
    }

    public static Metric createChooserMetric(Game game, MetricType metricCategory, String name, String description, List<String> choices) {
        JsonArray range = new JsonArray();

        for (String choice : choices) {
            JsonObject choiceObj = new JsonObject();
            choiceObj.addProperty("value", choice);
            range.add(choiceObj);
        }

        return new Metric(null, name, metricCategory.ordinal(), CHOOSER, JSON.getGson().toJson(range), game.getId());
    }

    public static Metric createCheckBoxMetric(Game game, MetricType metricCategory, String name, String description, List<String> values) {
        JsonObject data = new JsonObject();
        JsonElement jsonElements = JSON.getGson().toJsonTree(values);
        data.add("values", jsonElements);

        String str_data = JSON.getGson().toJson(data);


        return new Metric(null,
                name,
                metricCategory.ordinal(),
                CHECK_BOX,
                str_data,
                game.getId());
    }

    public static enum MetricType {
        MATCH_PERF_METRICS, ROBOT_METRICS;
        public static final MetricType[] VALID_TYPES = values();
    }

}