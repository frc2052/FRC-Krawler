package com.team2052.frckrawler.database.structures;

public class SortKey implements Structure {
	
	public static final int MATCH_METRIC_TYPE = 1;
	public static final int PIT_METRIC_TYPE = 2;
	public static final int DRIVER_METRIC_TYPE = 3;
	
	private int metricType;
	private String column;
	
	public SortKey(int _metricType, String _column) {
		metricType = _metricType;
		column = _column;
	}
	
	public String getColumn() {
		return column;
	}
	
	public int getMetricType() {
		return metricType;
	}
}
