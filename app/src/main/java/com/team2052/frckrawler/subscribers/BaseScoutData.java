package com.team2052.frckrawler.subscribers;

import com.team2052.frckrawler.database.MetricValue;
import com.team2052.frckrawler.db.Robot;

import java.util.List;

/**
 * Created by Adam on 5/5/2016.
 */
public class BaseScoutData {
    private List<MetricValue> metricValues;
    private List<Robot> robots;
    private String comment;

    public BaseScoutData(List<MetricValue> metrics, List<Robot> robots, String comment) {
        this.metricValues = metrics;
        this.robots = robots;
        this.comment = comment;
    }

    public List<MetricValue> getMetricValues() {
        return metricValues;
    }

    public String getComment() {
        return comment;
    }

    public List<Robot> getRobots() {
        return robots;
    }
}
