package com.team2052.frckrawler.database;

import com.team2052.frckrawler.db.Metric;

public class MetricValue {
    private Metric metric;
    private String value;

    public MetricValue(Metric metric, String value) {
        this.metric = metric;
        this.value = value;
    }

    public Metric getMetric() {
        return metric;
    }

    public String getValue() {
        return value;
    }

}
