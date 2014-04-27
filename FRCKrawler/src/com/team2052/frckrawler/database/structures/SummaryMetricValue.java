package com.team2052.frckrawler.database.structures;

public class SummaryMetricValue implements Structure {
	private int id;
	private String[] value;
	private int[] chooserCounts;
	
	public SummaryMetricValue(int _id, String[] _value, int[] _chooserCounts) {
		id = _id;
		value = _value;
		chooserCounts = _chooserCounts;
	}
	
	public SummaryMetricValue(int _id, String[] _value) {
		this(_id, _value, null);
	}
	
	public int getId() {
		return id;
	}

	public String[] getValue() {
		return value;
	}

	public int[] getChooserCounts() {
		return chooserCounts;
	}
}
