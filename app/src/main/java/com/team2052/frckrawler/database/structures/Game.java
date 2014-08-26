package com.team2052.frckrawler.database.structures;

public class Game implements Structure {

    private String name;

    public Game(String _name) {
        name = _name;
    }

    public String getName() {
        return name;
    }
}
