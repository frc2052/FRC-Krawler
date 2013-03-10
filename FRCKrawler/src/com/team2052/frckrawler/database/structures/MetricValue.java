package com.team2052.frckrawler.database.structures;

import com.team2052.frckrawler.database.DBContract;

public class MetricValue implements Structure {
	
	private Metric metric;
	private String[] value;
	
	public MetricValue(Metric _metric, String[] _value) throws MetricTypeMismatchException {
		
		if(_metric.getType() == DBContract.COUNTER || _metric.getType() == DBContract.SLIDER) {
			
			for(String v : _value) {
				try {
					Integer.parseInt(v);
				} catch (Exception e) {
					throw new MetricTypeMismatchException();
				}
			}
			
		}
			
		metric = _metric;
		value = _value;
	}
	
	public String getValueAsString() {
		
		if(value == null)
			return new String();
		
		String returnString = new String();
		
		for(int i = 0; i < value.length; i++) {
			
			returnString += value[i] + ":";
		}
		
		return returnString;
	}
	
	public Metric getMetric() {
		return metric;
	}
	
	public String[] getValue() {
		return value;
	}
	
	public class MetricTypeMismatchException extends Exception {}
}
