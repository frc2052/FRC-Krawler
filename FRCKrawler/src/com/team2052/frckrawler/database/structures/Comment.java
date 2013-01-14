package com.team2052.frckrawler.database.structures;

import java.util.Date;

public class Comment implements Structure {
	
	private int team;
	private int userID;
	private int eventID;
	private String text;
	private Date timeStamp;
	
	public Comment(int _team, int _userID, int _eventID, String _text, Date _timeStamp) {
		
		team = _team;
		userID = _userID;
		eventID = _eventID;
		text = _text;
		timeStamp = _timeStamp;
	}
	
	public int getTeamNumber() {
		return team;
	}
	
	public int getUserID() {
		return userID;
	}
	
	public int getEventID() {
		return eventID;
	}
	
	public String getText() {
		return text;
	}
	
	public Date getTimeStamp() {
		return timeStamp;
	}
}
