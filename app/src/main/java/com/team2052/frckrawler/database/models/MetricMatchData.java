package com.team2052.frckrawler.database.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.team2052.frckrawler.database.MetricValue;
import com.team2052.frckrawler.database.serializers.StringArrayDeserializer;

import java.io.Serializable;

/**
 * @author Adam
 */
@Table(name = "metricmatchdata")
public class MetricMatchData extends Model implements Serializable {

    @Column(name = "Robot", onDelete = Column.ForeignKeyAction.CASCADE)
    public Robot robot;

    @Column(name = "Metric", onDelete = Column.ForeignKeyAction.CASCADE)
    public Metric metric;

    @Column(name = "MetricValue")
    public String data;

    @Column(name = "Match", onDelete = Column.ForeignKeyAction.CASCADE)
    public Match match;

    public MetricMatchData(Robot robot, Metric metric, MetricValue values, Match match) {
        this.robot = robot;
        this.metric = metric;
        this.data = StringArrayDeserializer.deserialize(values.getValue());
        this.match = match;
    }

    public MetricMatchData() {
    }
}
