package com.team2052.frckrawler.listitems;

/**
 * Created by Adam on 8/22/2014.
 */
public abstract class ListElement implements ListItem {
    protected final String key;

    public ListElement() {
        key = "";
    }

    public ListElement(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
