package com.team2052.frckrawler.subscribers;

/**
 * Created by Adam on 6/23/2016.
 */

public class NoParseSubscriber<T> extends BaseDataSubscriber<T, T>{
    @Override
    public void parseData() {
        dataToBind = data;
    }
}
