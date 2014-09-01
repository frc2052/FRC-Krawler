package com.team2052.frckrawler.database.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.team2052.frckrawler.database.DBManager;

import java.io.Serializable;

/**
 * @author Adam
 */
@Table(name = "matches")
public class Match extends Model implements Serializable {
    //To avoid duplicates between Client and Server
    @Column(name = "key")
    public String key;

    @Column(name = "MatchType")
    public String matchType;

    @Column(name = "MatchNumber")
    public int matchNumber;

    @Column(name = "Event", onDelete = Column.ForeignKeyAction.CASCADE)
    public Event event;

    @Column(name = "Alliance", onDelete = Column.ForeignKeyAction.CASCADE)
    public Alliance alliance;

    public Match(String key, Alliance alliance, String matchType, int matchNumber, Event event) {
        this.key = key;
        this.alliance = alliance;
        this.matchType = matchType;
        this.matchNumber = matchNumber;
        this.event = event;
    }

    public Match() {
    }

    public Long saveAll(){
        alliance.blue1.save();
        alliance.blue2.save();
        alliance.blue3.save();
        alliance.red1.save();
        alliance.red2.save();
        alliance.red3.save();
        alliance.save();
        event.save();
        return save();
    }
}
