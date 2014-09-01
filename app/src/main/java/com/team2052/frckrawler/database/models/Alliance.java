package com.team2052.frckrawler.database.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.team2052.frckrawler.database.DBManager;

import java.io.Serializable;

/**
 * @author Adam
 */
@Table(name = "alliances")
public class Alliance extends Model implements Serializable{
    @Column(name = "remote_id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private int remoteId;

    @Column(name = "Red1", onDelete = Column.ForeignKeyAction.CASCADE)
    public Team red1;

    @Column(name = "Red2", onDelete = Column.ForeignKeyAction.CASCADE)
    public Team red2;

    @Column(name = "Red3", onDelete = Column.ForeignKeyAction.CASCADE)
    public Team red3;

    @Column(name = "Blue1", onDelete = Column.ForeignKeyAction.CASCADE)
    public Team blue1;

    @Column(name = "Blue2", onDelete = Column.ForeignKeyAction.CASCADE)
    public Team blue2;

    @Column(name = "Blue3", onDelete = Column.ForeignKeyAction.CASCADE)
    public Team blue3;

    @Column(name = "RedScore")
    public int redScore;

    @Column(name = "BlueScore")
    public int blueScore;

    public Alliance(Team red1, Team red2, Team red3, Team blue1, Team blue2, Team blue3, int redScore, int blueScore) {
        this.remoteId = DBManager.generateRemoteId();
        this.red1 = red1;
        this.red2 = red2;
        this.red3 = red3;
        this.blue1 = blue1;
        this.blue2 = blue2;
        this.blue3 = blue3;
        this.redScore = redScore;
        this.blueScore = blueScore;
    }

    public int setRemoteId(){
        return remoteId = DBManager.generateRemoteId();
    }

    public Alliance() {
    }
}
