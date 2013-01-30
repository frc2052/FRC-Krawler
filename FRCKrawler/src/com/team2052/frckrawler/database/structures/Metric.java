package com.team2052.frckrawler.database.structures;

public class Metric implements Structure {
	
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
	 * COUNTER - first value is the lower limit, second is the upper. They are expected to be numbers.
	 * SLIDER - first value is the lower limit, second is the upper. They are expected to be numbers.
	 * CHOOSER - all the choosable values that the user can select
	 * TEXT - not used, set to null
	 */
	
	public Metric(String _gameName, String _metricName, 
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
	
	public class MetricFactory {
		
		/*****
		 * Class: MatchPerformanceFactory
		 * 
		 * Summary: This provides easier creation of metrics. Instead of 
		 * having to know if you can use an array, and the type number 
		 * for your metrics, you can use the factory.
		 *****/
		
		public Metric createBooleanMetric
		(String game, String name, String description) {
			
			return createBooleanMetric(game, name, description, null);
		}
		
		public Metric createBooleanMetric
		(String game, String name, String description, String key) {
			
			return new Metric(game, name, description, key, 0, null);
		}
		
		public Metric createCounterMetric
		(String game, String name, String description, String[] lowerAndHigherBounds) {
			
			return createCounterMetric(game, name, description, lowerAndHigherBounds, null);
		}
		
		public Metric createCounterMetric
		(String game, String name, String description, String[] lowerAndHigherBounds, String key) {
			
			return new Metric(game, name, description, key, 1, lowerAndHigherBounds);
		}
		
		public Metric createSliderMetric
		(String game, String name, String description, String[] lowerAndHigherBounds) {
			
			return createSliderMetric(game, name, description, lowerAndHigherBounds, null);
		}
		
		public Metric createSliderMetric
		(String game, String name, String description, String[] lowerAndHigherBounds, String key) {
			
			return new Metric(game, name, description, key, 2, lowerAndHigherBounds);
		}
		
		public Metric createChooserMetric
		(String game, String name, String description, String[] choices) {
			
			return createCounterMetric(game, name, description, choices, null);
		}
		
		public Metric createChooserMetric
		(String game, String name, String description, String[] choices, String key) {
			
			return new Metric(game, name, description, key, 3, choices);
		}
		
		public Metric createTextMetric
		(String game, String name, String description) {
			
			return createBooleanMetric(game, name, description, null);
		}
		
		public Metric createTextMetric
		(String game, String name, String description, String key) {
			
			return new Metric(game, name, description, key, 4, null);
		}
	}
}
