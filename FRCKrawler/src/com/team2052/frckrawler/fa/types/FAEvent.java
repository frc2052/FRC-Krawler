package com.team2052.frckrawler.fa.types;

public class FAEvent {
	private String id;
	private String name;
	private String api_name;
	private String start_date;
	private String end_date;
	private String rank_date;
	
	public FAEvent() {
		
	}
	
	public String getID() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
