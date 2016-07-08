package com.team2052.frckrawler.subscribers;

import com.google.common.collect.Lists;
import com.team2052.frckrawler.listitems.ListItem;
import com.team2052.frckrawler.listitems.elements.KeyValueListElement;

import java.util.List;
import java.util.Map;

public class KeyValueListSubscriber extends BaseDataSubscriber<Map<String, String>, List<ListItem>> {
    @Override
    public void parseData() {
        dataToBind = Lists.newArrayList();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            dataToBind.add(new KeyValueListElement(entry));
        }
    }
}
