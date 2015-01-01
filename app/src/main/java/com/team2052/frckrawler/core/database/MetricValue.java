package com.team2052.frckrawler.core.database;

import com.team2052.frckrawler.db.MatchData;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.db.PitData;

public class MetricValue {
    private Metric metric;
    private String value;

    public MetricValue(Metric metric, String value) {
        this.metric = metric;
        this.value = value;
    }

    public MetricValue(MatchData matchData) {
        this(matchData.getMetric(), matchData.getData());
    }

    public MetricValue(PitData matchData) {
        this(matchData.getMetric(), matchData.getData());
    }

    public Metric getMetric() {
        return metric;
    }

    public String getValue() {
        return value;
    }

}
