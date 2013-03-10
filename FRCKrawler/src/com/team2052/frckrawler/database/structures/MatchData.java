package com.team2052.frckrawler.database.structures;

public class MatchData implements Structure {
	
	private int matchID;
	private int event;
	private int matchNum;
	private int robot;
	private int user;
	private String matchType;
	private String comments;
	private MetricValue[] values;
	
	public MatchData(int _matchID, int _event, int _matchNum, int _robot, int _user, 
			String _matchType, String _comments, MetricValue[] _values) {
		
		matchID = _matchID;
		event = _event;
		matchNum = _matchNum;
		robot = _robot;
		user = _user;
		matchType = _matchType;
		comments = _comments;
		values = _values;
	}
	
	public int getMatchID() {
		return matchID;
	}
	
	public int getEventID() {
		return event;
	}
	
	public int getMatchNumber() {
		return matchNum;
	}
	
	public int getRobotID() {
		return robot;
	}
	
	public int getUserID() {
		return user;
	}
	
	public String getMatchType() {
		return matchType;
	}
	
	public String getComments() {
		return comments;
	}
	
	public MetricValue[] getMetricValues() {
		return values;
	}
}
