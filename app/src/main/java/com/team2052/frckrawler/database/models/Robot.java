package com.team2052.frckrawler.database.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * @author Adam
 */
@Table(name = "robots")
public class Robot extends Model{
    @Column(name = "TeamNumber")
    public int teamNumber;

    @Column(name = "Comments")
    public String comments;

    @Column(name = "Opr")
    public double opr;

    @Column(name = "Game", onDelete = Column.ForeignKeyAction.CASCADE)
    public Game game;
}
