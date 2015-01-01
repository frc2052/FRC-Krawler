package com.team2052.frckrawler.core.listitems;

/**
 * @author Adam
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
