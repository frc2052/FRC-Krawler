package com.team2052.frckrawler.firebasemodels;

import com.team2052.frckrawler.db.Metric;

public class FirebaseMetric {
    public String name;
    public Long type;
    public String data;

    public FirebaseMetric() {
    }

    public Metric toDbMetric() {
        Metric metric = new Metric(null);

        metric.setName(name);
        metric.setType(type.intValue());
        metric.setData(data.substring(1, data.length() - 1));

        return metric;
    }
}
