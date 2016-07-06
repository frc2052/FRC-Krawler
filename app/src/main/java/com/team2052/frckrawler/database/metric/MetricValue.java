package com.team2052.frckrawler.database.metric;

import com.google.gson.JsonElement;
import com.team2052.frckrawler.db.Metric;

public class MetricValue {
    private Metric metric;
    private JsonElement value;

    public MetricValue(Metric metric, JsonElement value) {
        if (metric == null)
            throw new IllegalStateException("Metric cannot be null");
        this.metric = metric;
        this.value = value;
    }

    public Metric getMetric() {
        return metric;
    }

    public JsonElement getValue() {
        return value;
    }

}
