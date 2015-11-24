package com.team2052.frckrawler.database.subscribers;

import com.team2052.frckrawler.db.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adam on 11/24/2015.
 */
public class EventStringSubscriber extends BaseDataSubscriber<List<Event>, List<String>> {
    @Override
    public void parseData() {
        dataToBind = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            Event event = data.get(i);
            dataToBind.add(event.getGame().getName() + ", " + event.getName());
        }
    }
}
