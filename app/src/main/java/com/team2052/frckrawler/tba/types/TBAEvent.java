package com.team2052.frckrawler.tba.types;

public class TBAEvent {
    private String key;
    private String short_name;

    public TBAEvent() {
    }

    public String getID() {
        return key;
    }

    public String getName() {
        return short_name;
    }

    @Override
    public String toString() {
        return short_name;
    }
}
