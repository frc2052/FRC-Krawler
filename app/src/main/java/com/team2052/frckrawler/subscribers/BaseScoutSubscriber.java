package com.team2052.frckrawler.subscribers;

public class BaseScoutSubscriber extends BaseDataSubscriber<BaseScoutData, BaseScoutData> {
    @Override
    public void parseData() {
        dataToBind = data;
    }

}
