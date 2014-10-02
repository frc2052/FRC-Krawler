package com.team2052.frckrawler.database;

import com.team2052.frckrawler.activity.MetricsActivity;
import com.team2052.frckrawler.database.models.*;

/**
 * @author Adam
 */
public class MetricFactory {
    public static Metric createBooleanMetric(Game game, MetricsActivity.MetricType metricCategory, String name, String description) {
        return new Metric(game, metricCategory, name, description, Metric.BOOLEAN, new Object[]{});
    }

    public static Metric createCounterMetric(Game game, MetricsActivity.MetricType metricCategory, String name, String description, int min, int max, int incrementation) {
        return new Metric(game, metricCategory, name, description, Metric.COUNTER, new Integer[]{min, max, incrementation});
    }

    public static Metric createSliderMetric(Game game, MetricsActivity.MetricType metricCategory, String name, String description, int min, int max) {
        return new Metric(game, metricCategory, name, description, Metric.SLIDER, new Integer[]{min, max});
    }

    public static Metric createChooserMetric(Game game, MetricsActivity.MetricType metricCategory, String name, String description, String[] choices) {
        return new Metric(game, metricCategory, name, description, Metric.CHOOSER, choices);
    }
}
