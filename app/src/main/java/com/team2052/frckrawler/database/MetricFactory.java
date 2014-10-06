package com.team2052.frckrawler.database;

import com.team2052.frckrawler.activity.MetricsActivity;
import com.team2052.frckrawler.database.serializers.StringArrayDeserializer;

import frckrawler.Game;
import frckrawler.Metric;

/**
 * @author Adam
 */
public class MetricFactory
{
    public static Metric createBooleanMetric(Game game, MetricsActivity.MetricType metricCategory, String name, String description)
    {
        return new Metric(null, name, metricCategory.ordinal(), description, MetricValues.BOOLEAN, "", game.getId());

    }

    public static Metric createCounterMetric(Game game, MetricsActivity.MetricType metricCategory, String name, String description, int min, int max, int incrementation)
    {
        return new Metric(null, name, metricCategory.ordinal(), description, MetricValues.COUNTER, StringArrayDeserializer.deserialize(new String[]{Integer.toString(min), Integer.toString(max), Integer.toString(incrementation)}), game.getId());
    }

    public static Metric createSliderMetric(Game game, MetricsActivity.MetricType metricCategory, String name, String description, int min, int max)
    {
        return new Metric(null, name, metricCategory.ordinal(), description, MetricValues.SLIDER, StringArrayDeserializer.deserialize(new String[]{Integer.toString(min), Integer.toString(max)}), game.getId());
    }

    public static Metric createChooserMetric(Game game, MetricsActivity.MetricType metricCategory, String name, String description, String[] choices)
    {
        return new Metric(null, name, metricCategory.ordinal(), description, MetricValues.CHOOSER, StringArrayDeserializer.deserialize(choices), game.getId());
    }
}
