package com.team2052.frckrawler.database.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.team2052.frckrawler.activity.MetricsActivity;

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

    @Column(name = "Name")
    public String name;

    @Column(name = "Category")
    public int category;

    @Column(name = "Description")
    public String description;

    @Column(name = "Type")
    public int type;
mk
    @Column(name = "Range")
    public String range;

    @Column(name = "Display")
    public boolean display;

    @Column(name = "Game", onDelete = Column.ForeignKeyAction.CASCADE)
    public Game game;

    public Metric(Game game, MetricsActivity.MetricType metricCategory, String name, String description, int type, Object[] range, boolean display) {
        this.game = game;
        this.name = name;
        this.category = metricCategory.ordinal();
        this.description = description;
        this.type = type;
        this.range = unparseRange(range);
        this.display = display;
    }

    public Metric() {
    }

    public Object[] parseRange() {
        String currentRangeValString = "";
        ArrayList<Object> rangeArrList = new ArrayList<Object>();
        for (int character = 0; character < range.length(); character++) {
            if (range.charAt(character) != ':')
                currentRangeValString += range.charAt(character);
            else {
                rangeArrList.add(currentRangeValString);
                currentRangeValString = new String();
            }
        }
        return rangeArrList.toArray();
    }

    public static String unparseRange(Object[] range) {
        String rangeInput = "";
        for (Object obj : range) {
            rangeInput += obj.toString() + ":";
        }
        return rangeInput;
    }


}
