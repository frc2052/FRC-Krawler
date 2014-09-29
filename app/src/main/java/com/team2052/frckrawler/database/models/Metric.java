package com.team2052.frckrawler.database.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.*;
import com.team2052.frckrawler.activity.MetricsActivity;
import com.team2052.frckrawler.database.DBManager;

import java.io.Serializable;

/**
 * @author Adam
 */
@Table(name = "metrics")
public class Metric extends Model implements Serializable
{
    public static final int BOOLEAN = 0;
    public static final int COUNTER = 1;
    public static final int SLIDER = 2;
    public static final int CHOOSER = 3;
    public static final int TEXT = 4;
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
    @Column(name = "Game", onDelete = Column.ForeignKeyAction.CASCADE)
    public Game game;
    //To avoid duplicates between Client and Server
    @Column(name = "remote_id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private int remoteId;

    public Metric(Game game, MetricsActivity.MetricType metricCategory, String name, String description, int type, Object[] range)
    {
        this.remoteId = DBManager.generateRemoteId();
        this.game = game;
        this.name = name;
        this.category = metricCategory.ordinal();
        this.description = description;
        this.type = type;
        this.range = range;
    }

    public Metric()
    {
    }
}
