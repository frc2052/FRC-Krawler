package com.team2052.frckrawler.database.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.team2052.frckrawler.database.MetricValue;

/**
 * @author Adam
 */
@Table(name = "matchdata")
public class MatchData extends Model {

    @Column(name = "Robot", onDelete = Column.ForeignKeyAction.CASCADE)
    public Robot robot;

    @Column(name = "Metric", onDelete = Column.ForeignKeyAction.CASCADE)
    public Metric metric;

    @Column(name = "Data")
    public String[] data;

    @Column(name = "Match", onDelete = Column.ForeignKeyAction.CASCADE)
    public Match match;

    public MatchData(Robot robot, Metric metric, MetricValue values, Match match) {
        this.robot = robot;
        this.metric = metric;
        this.data = values.getValue();
        this.match = match;
    }

    public MatchData() {
    }
}
