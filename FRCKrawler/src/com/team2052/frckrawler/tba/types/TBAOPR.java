package com.team2052.frckrawler.tba.types;

import java.text.DecimalFormat;

//***** FIX ME FOR OPR WHEN TBA SUPPORTS IT ****//

public class TBAOPR {
	
	private double OPR;
	private int teamNum;
	
	public TBAOPR(int _teamNum, int _value) {
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
