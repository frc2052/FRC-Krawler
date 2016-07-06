package com.team2052.frckrawler.subscribers;

import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.listitems.ListItem;
import com.team2052.frckrawler.listitems.elements.EventListElement;

import java.util.ArrayList;
import java.util.List;

public class EventListSubscriber extends BaseDataSubscriber<List<Event>, List<ListItem>> {
    @Override
    public void parseData() {
        dataToBind = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) dataToBind.add(new EventListElement(data.get(i)));
    }
}
