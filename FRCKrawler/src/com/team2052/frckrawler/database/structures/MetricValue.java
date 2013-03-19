package com.team2052.frckrawler.database.structures;


import java.text.DecimalFormat;

import com.team2052.frckrawler.database.DBContract;

public class MetricValue implements Structure {
	
	private Metric metric;
	private String[] value; //Array only used for 
	
	public MetricValue(Metric _metric, String[] _value) throws MetricTypeMismatchException {
		
		if(_metric.getType() == DBContract.COUNTER || _metric.getType() == DBContract.SLIDER) {
			
			for(String v : _value) {
				try {
					Double.parseDouble(v);
				} catch (Exception e) {
					throw new MetricTypeMismatchException();
				}
			}
		}
			
		metric = _metric;
		value = _value;
	}
	
	public String getValueAsDBReadableString() {
		
		if(value == null)
			return new String();
		
		String returnString = new String();
		
		for(int i = 0; i < value.length; i++) {
			
			returnString += value[i] + ":";
		}
		
		return returnString;
	}
	
	public String getValueAsHumanReadableString() {
		
		if(value == null)
			return new String();
		
		String returnString = new String();
		
		for(int i = 0; i < value.length; i++) {
			
			boolean isDecimal = true;
			
			try {
				Double.parseDouble(value[i]);
			} catch(NumberFormatException e) {
				isDecimal = false;
			}
			
			if(isDecimal) {
				
				DecimalFormat format = new DecimalFormat("0.000");
				
				if(i != value.length - 1)
					returnString += format.format
							(Double.parseDouble(value[i])) + ", ";
				else
					returnString += format.format(Double.parseDouble(value[i]));
			} else {
				
				if(i != value.length - 1)
					returnString += value[i] + ", ";
				else
					returnString += value[i];
			}
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
