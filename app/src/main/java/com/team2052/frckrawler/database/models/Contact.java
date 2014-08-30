package com.team2052.frckrawler.database.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * @author Adam
 */
@Table(name = "contacts")
public class Contact extends Model{
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

    public Contact(Team team, String name, String email, String address, String phoneNumber) {
        this.team = team;
        this.name = name;
        this.email = email;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    public Contact() {
    }
}
