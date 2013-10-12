package com.team2052.frckrawler.fa.types;

public class FAOPR {
	
	private double OPR;
	private int teamNum;
	
	public FAOPR(int _teamNum, int _value) {
		teamNum = _teamNum;
		OPR = _value;
	}
	
	public double getOPR() {
		return OPR;
	}
	
	public int getTeamNumber() {
		return teamNum;
	}
	
	@Override
	public String toString() {
		return OPR + "";
	}
}
