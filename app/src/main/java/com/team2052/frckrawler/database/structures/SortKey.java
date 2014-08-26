package com.team2052.frckrawler.database.structures;

public class SortKey implements Structure {

    public static final int MATCH_METRIC_TYPE = 1;
    public static final int PIT_METRIC_TYPE = 2;
    public static final int DRIVER_METRIC_TYPE = 3;
    public static final int OPR_TYPE = 4;

    private boolean isAscending;
    private int metricType;
    private int metricID;

    public SortKey(int _metricType, int _metricID) {
        this(_metricType, _metricID, false);
    }

    public SortKey(int _metricType, int _metricID, boolean _ascending) {
        isAscending = _ascending;
        metricType = _metricType;
        metricID = _metricID;
    }

    public int getMetricID() {
        return metricID;
    }

    public int getMetricType() {
        return metricType;
    }

    public boolean isAscending() {
        return isAscending;
    }
}
