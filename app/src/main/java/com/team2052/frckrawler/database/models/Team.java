package com.team2052.frckrawler.database.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * @author Adam
 */
@Table(name = "Teams")
public class Team extends Model {
    @Column(name = "Number", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public int number;

    @Column(name = "Name")
    public String name;

    @Column(name = "School")
    public String school;

    @Column(name = "City")
    public String city;

    @Column(name = "RookieYear")
    public int rookieYear;

    @Column(name = "Website")
    public String website;

    @Column(name = "PostalCode")
    public String postalCode;

    @Column(name = "Colors")
    public String colors;


    public Team(int number, String name, String school, String city, int rookieYear, String website, String postalCode, String colors) {
        super();
        this.number = number;
        this.name = name;
        this.school = school;
        this.city = city;
        this.rookieYear = rookieYear;
        this.website = website;
        this.postalCode = postalCode;
        this.colors = colors;
    }

    public Team() {}

    public Team(int number, String name) {
        this(number, name, null, null, -1, null, null, null);
    }

}
