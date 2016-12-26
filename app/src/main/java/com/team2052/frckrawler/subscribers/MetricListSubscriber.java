package com.team2052.frckrawler.subscribers;

import com.team2052.frckrawler.db.Metric;

import java.util.ArrayList;
import java.util.List;

public class MetricListSubscriber extends BaseDataSubscriber<List<Metric>, List<Object>> {
    @Override
    public void parseData() {
        dataToBind = new ArrayList<>(data);
    }
}
