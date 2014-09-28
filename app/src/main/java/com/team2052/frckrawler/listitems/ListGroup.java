package com.team2052.frckrawler.listitems;

import java.util.*;

/**
 * @author Adam
 */
public class ListGroup
{
    public final List<ListItem> children = new ArrayList<>();
    private final String name;

    public ListGroup(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
}
