package com.team2052.frckrawler.database.structures;

public class MatchPerformanceMetric implements Structure {
	
	private String gameName;
	private String metricName;
	private String description;
	private String key;
	private int type;	//See DatabaseContract for keys.
	private String[] range;
	/*
	 * The 'range' array is used different for different 'type' values
	 * 
	 * BOOLEAN - not used, set to null
	 * COUNTER - first value is the lower limit, second is the lower. They are expected to be numbers.
	 * SLIDER - first value is the lower limit, second is the lower. They are expected to be numbers.
	 * CHOOSER - all the choosable values that the user can select
	 * TEXT - not used, set to null
	 */
	
	public MatchPerformanceMetric(String _gameName, String _metricName, int _type) {
		
		this(_gameName, _metricName, null, _type);
	}

	public MatchPerformanceMetric (String _gameName, String _metricName, String _description, int _type) {
		
		this(_gameName, _metricName, _description, _type, null);
	}
	
	public MatchPerformanceMetric(String _gameName, String _metricName, 
			String _description, int _type, String[] _range) {
		
		this(_gameName, _metricName, _description, null, _type, _range);
	}
	
	public MatchPerformanceMetric(String _gameName, String _metricName, 
			String _description, String _key, int _type, String[] _range) {
		
		gameName = _gameName;
		metricName = _metricName;
		description = _description;
		key = _key;
		type = _type;
		range = _range;
		
		if(type == 0 || type == 4)
			range = null;
	}
	
	public String getGameName() {
		return gameName;
	}
	
	public String getMetricName() {
		return metricName;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getKey() {
		return key;
	}
	
	public int getType() {
		return type;
	}
	
	public String[] getRange() {
		return range;
	}
}
