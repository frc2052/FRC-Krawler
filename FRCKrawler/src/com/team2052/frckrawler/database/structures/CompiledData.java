package com.team2052.frckrawler.database.structures;

public class CompiledData implements Structure {
	
	private int event;
	private int[] matchesPlayed;
	private Robot robot;
	private String[] matchComments;
	private MetricValue[] compiledMatchData;
	private MetricValue[] compiledDriverData;
	
	public CompiledData(int _event, int[] _matchesPlayed, Robot _robot, 
			String[] _matchComments, MetricValue[] _compiledMatchData, 
			MetricValue[] _compiledDriverData) {
		
		event = _event;
		matchesPlayed = _matchesPlayed;
		robot = _robot;
		matchComments = _matchComments;
		compiledMatchData = _compiledMatchData;
		compiledDriverData = _compiledDriverData;
	}
	
	public int getEventID() {
		return event;
	}
	
	public int[] getMatchesPlayed() {
		return matchesPlayed;
	}
	
	public Robot getRobot() {
		return robot;
	}
	
	public String[] getMatchComments() {
		return matchComments;
	}
	
	public MetricValue[] getCompiledMatchData() {
		return compiledMatchData;
	}
	
	public MetricValue[] getCompiledDriverData() {
		return compiledDriverData;
	}
}
