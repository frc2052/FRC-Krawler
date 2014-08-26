package com.team2052.frckrawler.database.structures;

public class SummaryCompiledData {
    private int teamNumber;
    private boolean isChecked;
    private float opr;
    private SummaryMetricValue[] matchData;
    private SummaryMetricValue[] robotData;

    public SummaryCompiledData(int _teamNumber, boolean _isChecked, float _opr,
                               SummaryMetricValue[] _matchData, SummaryMetricValue[] _robotData) {
        teamNumber = _teamNumber;
        isChecked = _isChecked;
        opr = _opr;
        matchData = _matchData;
        robotData = _robotData;
    }

    public int getTeamNumber() {
        return teamNumber;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public float getOpr() {
        return opr;
    }

    public SummaryMetricValue[] getMatchData() {
        return matchData;
    }

    public SummaryMetricValue[] getRobotData() {
        return robotData;
    }
}
