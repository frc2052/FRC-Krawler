package com.team2052.frckrawler.subscribers;

import com.google.common.collect.Lists;
import com.team2052.frckrawler.listitems.ListItem;
import com.team2052.frckrawler.listitems.elements.SimpleListElement;

import java.util.List;

public class StringListSubscriber extends BaseDataSubscriber<List<String>, List<ListItem>> {
    @Override
    public void parseData() {
        dataToBind = Lists.newArrayList();
        for (String entry : data) {
            dataToBind.add(new SimpleListElement(entry, entry));
        }
    }
}
