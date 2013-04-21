package com.team2052.frckrawler.database.structures;

import java.util.ArrayList;

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
	
	public String[] getMetricsAsStrings() {
		
		ArrayList<String> metricsList = new ArrayList<String>();
		
		metricsList.add("Team Number");
		metricsList.add("Matches Played");
		metricsList.add("Robot Comments");
		metricsList.add("Match Comments");
		
		for(int i = 0; i < compiledMatchData.length; i++)
			if(compiledMatchData[i].getMetric().isDisplayed())
				metricsList.add(compiledMatchData[i].getMetric().getMetricName().
						replace(',', ' '));
		
		for(int i = 0; i < robot.getMetrics().length; i++)
			if(robot.getMetrics()[i].isDisplayed())
				metricsList.add(robot.getMetrics()[i].getMetricName().
						replace(',', ' '));
		
		return metricsList.toArray(new String[0]);
	}
	
	public String[] getValuesAsStrings() {
		
		ArrayList<String> valsList = new ArrayList<String>();
		
		String matchComments = new String();
		for(int i = 0; i < getMatchComments().length; i++)
			matchComments += matchesPlayed[i] + ": " + getMatchComments()[i] + " ";
		
		valsList.add(Integer.toString(robot.getTeamNumber()));
		valsList.add(Integer.toString(matchesPlayed.length));
		valsList.add(robot.getComments().replace(',', ' '));
		valsList.add(matchComments.replace(',', ' '));
		
		for(int i = 0; i < compiledMatchData.length; i++)
			if(compiledMatchData[i].getMetric().isDisplayed())
				valsList.add(compiledMatchData[i].getValueAsHumanReadableString().
					replace(',', ' '));
		
		for(int i = 0; i < robot.getMetricValues().length; i++)
			if(robot.getMetricValues()[i].getMetric().isDisplayed())
				valsList.add(robot.getMetricValues()[i].getValueAsHumanReadableString().
					replace(',', ' '));
		
		return valsList.toArray(new String[0]);
	}
}
