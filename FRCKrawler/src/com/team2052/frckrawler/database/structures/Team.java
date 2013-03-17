package com.team2052.frckrawler.database.structures;

public class Team implements Structure {
	
	private int number;
	private String name;
	private String school;
	private String city;
	private int rookieYear;
	private String website;
	private String statePostalCode;
	private String colors;
	
	public Team(int _number, String _name) {
		
		this(_number, _name, null, null, -1, null, null, null);
	}
	
	public Team(int _number, String _name, String _school, String _city, 
			int _rookieYear, String _website, String _statePostalCode, String _colors) {
		
		number = _number;
		name = _name;
		school = _school;
		city = _city;
		rookieYear = _rookieYear;
		website = _website;
		statePostalCode = _statePostalCode;
		colors = _colors;
	}
	
	public int getNumber() {
		return number;
	}
	
	public String getName() {
		return name;
	}
	
	public String getSchool() {
		return school;
	}
	
	public String getCity() {
		return city;
	}
	
	public int getRookieYear() {
			return rookieYear;
	}
	
	public String getWebsite() {
		return website;
	}
	
	public String getStatePostalCode() {
		return statePostalCode;
	}
	
	public String getColors() {
		return colors;
	}
}
