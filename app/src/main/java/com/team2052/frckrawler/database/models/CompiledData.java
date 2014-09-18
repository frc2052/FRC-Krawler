package com.team2052.frckrawler.database.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Table;

/**
 * @author Adam
 */
@Table(name = "compileddata")
public class CompiledData extends Model {
    public String data;
    public Metric metric;
    public Robot robot;
}
