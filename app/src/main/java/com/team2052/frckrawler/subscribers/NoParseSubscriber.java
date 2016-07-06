package com.team2052.frckrawler.subscribers;

public class NoParseSubscriber<T> extends BaseDataSubscriber<T, T> {
    @Override
    public void parseData() {
        dataToBind = data;
    }
}
