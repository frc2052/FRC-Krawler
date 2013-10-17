package com.team2052.frckrawler.database.structures;

/*****
 * Class: Query
 * 
 * @author Charles Hofer
 *
 * Summary: 
 */

public class Query implements Structure {
	
	public static final int TYPE_ROBOT = 1;
	public static final int TYPE_MATCH_DATA = 2;
	public static final int TYPE_DRIVER_DATA = 3;
	
	public static final int COMPARISON_EQUAL_TO = 1;
	public static final int COMPARISON_LESS_THAN = 2; //Only works with COUNTER and SLIDER
	public static final int COMPARISON_GREATER_THAN = 3; //Only works with COUNTER and SLIDER
	public static final int COMPARISON_CHOOSER_COMPARE = 4; //Only works with match data CHOOSERS
	
	private int type;
	private int metricID;
	private int comparison;
	private String metricValue;
	
	public Query(int _type, int _metricID, int _comparison, String _metricValue) {
		
		type = _type;
		metricID = _metricID;
		comparison = _comparison;
		metricValue = _metricValue;
	}
	
	public int getType() {
		return type;
	}
	
	public int getMetricID() {
		return metricID;
	}
	
	public int getComparison() {
		return comparison;
	}
	
	public String getMetricValue() {
		return metricValue;
	}
}
