package com.team2052.frckrawler.database.structures;

public class SortKey implements Structure {
	
	public static final int MATCH_METRIC_TYPE = 1;
	public static final int PIT_METRIC_TYPE = 2;
	public static final int DRIVER_METRIC_TYPE = 3;
	
	private boolean isAscending;
	private int metricType;
	private String column;
	
	public SortKey(int _metricType, String _column) {
		this(_metricType, _column, false);
	}
	
	public SortKey(int _metricType, String _column, boolean _ascending) {
		isAscending = _ascending;
		metricType = _metricType;
		column = _column;
	}
	
	public String getColumn() {
		return column;
	}
	
	public int getMetricType() {
		return metricType;
	}
	
	public boolean isAscending() {
		return isAscending;
	}
}
