package com.team2052.frckrawler.core.database;

import com.team2052.frckrawler.db.DaoSession;
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

    public MetricValue(DaoSession daoSession, long metric_id, String value) {
        this(daoSession.getMetricDao().load(metric_id), value);
    }

    public MetricValue(DaoSession daoSession, MatchData matchData) {
        this(daoSession, matchData.getMetricId(), matchData.getData());
    }

    public MetricValue(DaoSession daoSession, PitData matchData) {
        this(daoSession, matchData.getMetricId(), matchData.getData());
    }

    public Metric getMetric() {
        return metric;
    }

    public String getValue() {
        return value;
    }

}
