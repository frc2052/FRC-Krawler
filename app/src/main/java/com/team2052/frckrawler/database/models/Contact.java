package com.team2052.frckrawler.database.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.team2052.frckrawler.database.DBManager;

import java.io.Serializable;

/**
 * @author Adam
 */
@Table(name = "contacts")
public class Contact extends Model implements Serializable {
    @Column(name = "Team", onDelete = Column.ForeignKeyAction.CASCADE)
    public Team team;
    @Column(name = "Name")
    public String name;
    @Column(name = "Email")
    public String email;
    @Column(name = "Address")
    public String address;
    @Column(name = "PhoneNumber")
    public String phoneNumber;
    @Column(name = "remote_id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private int remoteId;

    public Contact(Team team, String name, String email, String address, String phoneNumber) {
        this.remoteId = DBManager.generateRemoteId();
        this.team = team;
        this.name = name;
        this.email = email;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    public Contact() {
    }
}
