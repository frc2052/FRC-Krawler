package com.team2052.frckrawler.util;

public class Tuple<V> {

    private double key;
    private V value;

    public Tuple(double _key, V _value) {
        key = _key;
        value = _value;
    }

    public double getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }
}
