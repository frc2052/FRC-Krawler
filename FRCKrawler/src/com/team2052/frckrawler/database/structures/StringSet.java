package com.team2052.frckrawler.database.structures;

public class StringSet implements Structure {
	
	String key;
	String value;
	
	public StringSet(String _key, String _value) {
		key = _key;
		value = _value;
	}
	
	public String getKey() {
		return key;
	}
	
	public String getValue() {
		return value;
	}
}
