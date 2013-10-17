package com.team2052.frckrawler.fa.types;

import java.text.DecimalFormat;

public class FAOPR {
	
	private double OPR;
	private int teamNum;
	
	public FAOPR(int _teamNum, int _value) {
		teamNum = _teamNum;
		OPR = _value;
	}
	
	public double getOPR() {
		DecimalFormat format = new DecimalFormat("0.00");
		return Double.parseDouble(format.format(OPR));
	}
	
	public int getTeamNumber() {
		return teamNum;
	}
	
	@Override
	public String toString() {
		return OPR + "";
	}
}
