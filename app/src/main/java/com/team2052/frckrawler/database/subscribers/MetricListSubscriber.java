package com.team2052.frckrawler.database.subscribers;

import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.listitems.ListItem;
import com.team2052.frckrawler.listitems.elements.MetricListElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by adam on 11/18/15.
 */
public class MetricListSubscriber extends BaseDataSubscriber<List<Metric>, List<ListItem>> {
    @Override
    public void parseData() {
        dataToBind = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) dataToBind.add(new MetricListElement(data.get(i)));
    }
}
