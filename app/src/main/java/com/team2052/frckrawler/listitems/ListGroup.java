package com.team2052.frckrawler.listitems;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adam on 8/25/2014.
 */
public class ListGroup {
    public final List<ListItem> children = new ArrayList<ListItem>();
    private final String name;

    public ListGroup(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
