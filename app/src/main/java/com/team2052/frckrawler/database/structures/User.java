package com.team2052.frckrawler.database.structures;

public class User implements Structure {

    private boolean superuser;
    private int id; //When set to -1, this user has no ID yet.

    private String name;

    public User(String _name) {
        this(_name, false);
    }

    public User(String _name, boolean _superuser) {
        this(_name, _superuser, -1);
    }

    public User(String _name, int _superuser, int _id) {
        this(_name, _superuser > 0, _id);
    }

    public User(String _name, boolean _superuser, int _id) {
        name = _name;
        superuser = _superuser;
        id = _id;
    }

    public String getName() {
        return name;
    }

    public boolean isSuperuser() {

        return superuser;
    }

    public int getID() {
        return id;
    }
}
