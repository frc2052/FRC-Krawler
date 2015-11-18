package com.team2052.frckrawler.database.subscribers;

import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.listitems.ListItem;
import com.team2052.frckrawler.listitems.elements.EventListElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Acorp on 11/17/2015.
 */
public class EventListSubscriber extends BaseDataSubscriber<List<Event>, List<ListItem>> {
    @Override
    public void parseData() {
        dataToBind = new ArrayList<>();
        for (Event event : data) {
            dataToBind.add(new EventListElement(event));
        }
    }
}
