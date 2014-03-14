package com.team2052.frckrawler.database.structures;

public class Match implements Structure {
	private int matchNum;
	private int red1RobotID;
	private int red2RobotID;
	private int red3RobotID;
	private int blue1RobotID;
	private int blue2RobotID;
	private int blue3RobotID;
	private int redScore;
	private int blueScore;
	
	public Match(int _matchNum, int _red1RobotID, 
			int _red2RobotID, int _red3RobotID, 
			int _blue1RobotID, int _blue2RobotID, 
			int _blue3RobotID, int _redScore,
			int _blueScore) {
		matchNum = _matchNum;
		red1RobotID = _red1RobotID;
		red2RobotID = _red2RobotID;
		red3RobotID = _red3RobotID;
		blue1RobotID = _blue1RobotID;
		blue2RobotID = _blue2RobotID;
		blue3RobotID = _blue3RobotID;
		redScore = _redScore;
		blueScore = _blueScore;
	}
	
	public Match(int _matchNum, Robot[] robots,
			int _redScore, int _blueScore) {
		matchNum = _matchNum;
		red1RobotID = robots[0].getID();
		red2RobotID = robots[1].getID();
		red3RobotID = robots[2].getID();
		blue1RobotID = robots[3].getID();
		blue2RobotID = robots[4].getID();
		blue3RobotID = robots[5].getID();
		redScore = _redScore;
		blueScore = _blueScore;
	}
	
	public int getMatchNumber() {
		return matchNum;
	}
	
	public int getRed1RobotID() {
		return red1RobotID;
	}
	
	public int getRed2RobotID() {
		return red2RobotID;
	}
	
	public int getRed3RobotID() {
		return red3RobotID;
	}
	
	public int getBlue1RobotID() {
		return blue1RobotID;
	}
	
	public int getBlue2RobotID() {
		return blue2RobotID;
	}
	
	public int getBlue3RobotID() {
		return blue3RobotID;
	}
	
	public int getRedScore() {
		return redScore;
	}
	
	public int getBlueScore() {
		return blueScore;
	}
	
	public int[] getRobotsIDs() {
		return new int[] {red1RobotID, red2RobotID, 
				red3RobotID, blue1RobotID, 
				blue2RobotID, blue3RobotID};
	}
}
