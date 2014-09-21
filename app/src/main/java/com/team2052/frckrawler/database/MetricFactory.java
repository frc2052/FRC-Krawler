package com.team2052.frckrawler.database;

import com.team2052.frckrawler.activity.MetricsActivity;
import com.team2052.frckrawler.database.models.Game;
import com.team2052.frckrawler.database.models.Metric;

/**
 * @author Adam
 */
public class MetricFactory {
    public static Metric createBooleanMetric(Game game, MetricsActivity.MetricType metricCategory, String name, String description, boolean displayed) {
        return new Metric(game, metricCategory, name, description, Metric.BOOLEAN, new Object[]{}, displayed);
    }

    public static Metric createCounterMetric(Game game, MetricsActivity.MetricType metricCategory, String name, String description, int min, int max, int incrementation, boolean displayed) {
        return new Metric(game, metricCategory, name, description, Metric.COUNTER, new Integer[]{Integer.valueOf(min), Integer.valueOf(max), Integer.valueOf(incrementation)}, displayed);
    }

    public static Metric createSliderMetric(Game game, MetricsActivity.MetricType metricCategory, String name, String description, int min, int max, boolean displayed) {
        return new Metric(game, metricCategory, name, description, Metric.SLIDER, new Integer[]{Integer.valueOf(min), Integer.valueOf(max)}, displayed);
    }

    public static Metric createChooserMetric(Game game, MetricsActivity.MetricType metricCategory, String name, String description, String[] choices, boolean displayed) {
        return new Metric(game, metricCategory, name, description, Metric.CHOOSER, choices, displayed);
    }

    public static Metric createTextMetric(Game game, MetricsActivity.MetricType metricCategory, String name, String description, boolean displayed) {
        return new Metric(game, metricCategory, name, description, Metric.TEXT, new Object[]{}, displayed);
    }

    public static Metric createMathMetric(Game game, MetricsActivity.MetricType metricCategory, String name, String description, Integer[] operendIDs, boolean displayed) {
        return new Metric(game, metricCategory, name, description, Metric.MATH, operendIDs, displayed);
    }
}
