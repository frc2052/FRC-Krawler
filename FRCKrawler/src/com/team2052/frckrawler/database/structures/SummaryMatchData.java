package com.team2052.frckrawler.database.structures;

public class SummaryMatchData implements Structure {
	private int matchNumber;
	private int teamNumber;
	private String comments;
	private SummaryMetricValue[] data;
	
	public SummaryMatchData(int matchNumber, int teamNumber,
			String comments, SummaryMetricValue[] data) {
		this.matchNumber = matchNumber;
		this.teamNumber = teamNumber;
		this.comments = comments;
		this.data = data;
	}
	
	public int getMatchNumber() {
		return matchNumber;
	}
	public int getTeamNumber() {
		return teamNumber;
	}
	public String getComments() {
		return comments;
	}
	public SummaryMetricValue[] getData() {
		return data;
	}
}
