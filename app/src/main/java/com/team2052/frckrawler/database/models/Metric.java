package com.team2052.frckrawler.database.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.team2052.frckrawler.activity.MetricsActivity;
import com.team2052.frckrawler.database.models.Game;

import java.util.ArrayList;

/**
 * @author Adam
 */
@Table(name = "metrics")
public class Metric extends Model {
    public static final int BOOLEAN = 0;
    public static final int COUNTER = 1;
    public static final int SLIDER = 2;
    public static final int CHOOSER = 3;
    public static final int TEXT = 4;
    public static final int MATH = 5;

    //To avoid duplicates between Client and Server
    @Column(name = "remote_id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public int remoteId;

    @Column(name = "Name")
    public String name;

    @Column(name = "Category")
    public int category;

    @Column(name = "Description")
    public String description;

    @Column(name = "Type")
    public int type;

    @Column(name = "Range")
    public Object[] range;

    @Column(name = "Display")
    public boolean display;

    @Column(name = "Game", onDelete = Column.ForeignKeyAction.CASCADE)
    public Game game;

    public Metric(Game game, MetricsActivity.MetricType metricCategory, String name, String description, int type, Object[] range, boolean display) {
        this.remoteId = (int)(Math.random() * 1000000);
        this.game = game;
        this.name = name;
        this.category = metricCategory.ordinal();
        this.description = description;
        this.type = type;
        this.range = range;
        this.display = display;
    }

    public Metric() {
    }
}
