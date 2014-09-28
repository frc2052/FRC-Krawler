package com.team2052.frckrawler.database.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.*;
import com.team2052.frckrawler.database.DBManager;

import java.io.Serializable;

/**
 * @author Adam
 */
@Table(name = "contacts")
public class Contact extends Model implements Serializable
{
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
    @Column(name = "TeamRole")
    public String teamRole;
    @Column(name = "remote_id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public int remoteId;

    public Contact(Team team, String name, String email, String address, String teamRole, String phoneNumber)
    {
        this.remoteId = DBManager.generateRemoteId();
        this.team = team;
        this.name = name;
        this.email = email;
        this.address = address;
        this.teamRole = teamRole;
        this.phoneNumber = phoneNumber;
    }

    public Contact()
    {
    }
}
