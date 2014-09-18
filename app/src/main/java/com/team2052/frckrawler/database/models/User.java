package com.team2052.frckrawler.database.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.team2052.frckrawler.database.DBManager;

import java.io.Serializable;

/**
 * @author Adam
 */
@Table(name = "users")
public class User extends Model implements Serializable {
    @Column(name = "Name")
    public String name;
    @Column(name = "remote_id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private int remoteId;

    public User(String name) {
        this.remoteId = DBManager.generateRemoteId();
        this.name = name;
    }

    public User() {
    }
}
