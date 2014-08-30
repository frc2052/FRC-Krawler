package com.team2052.frckrawler.database.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * @author Adam
 */
@Table(name = "matches")
public class Match extends Model{
    //To avoid duplicates between Client and Server
    @Column(name = "remote_id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public int remoteId;

    @Column(name = "MatchNumber")
    public int matchNumber;

    @Column(name = "Red1")
    public int red1;

    @Column(name = "Red2")
    public int red2;

    @Column(name = "Red3")
    public int red3;

    @Column(name = "Blue1")
    public int blue1;

    @Column(name = "Blue2")
    public int blue2;

    @Column(name = "BLue3")
    public int blue3;

    @Column(name = "RedScore")
    public int redScore;

    @Column(name = "BlueScore")
    public int blueScore;

    @Column(name = "Event", onDelete = Column.ForeignKeyAction.CASCADE)
    public Event event;

    public Match(int matchNumber, int red1, int red2, int red3, int blue1, int blue2, int blue3, int redScore, int blueScore) {
        this.remoteId = (int)(Math.random() * 1000000);
        this.matchNumber = matchNumber;
        this.red1 = red1;
        this.red2 = red2;
        this.red3 = red3;
        this.blue1 = blue1;
        this.blue2 = blue2;
        this.blue3 = blue3;
        this.redScore = redScore;
        this.blueScore = blueScore;
    }

    public Match() {
    }
}
