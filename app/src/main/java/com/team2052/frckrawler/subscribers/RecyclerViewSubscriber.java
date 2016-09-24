package com.team2052.frckrawler.subscribers;

import java.util.List;

public class RecyclerViewSubscriber extends BaseDataSubscriber<List<Object>, List<Object>>{
    @Override
    public void parseData() {
        dataToBind = data;
    }
}
