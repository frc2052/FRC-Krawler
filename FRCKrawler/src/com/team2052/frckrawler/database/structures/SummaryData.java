package com.team2052.frckrawler.database.structures;

public class SummaryData extends CompiledData {
	
	private MatchData[] matchData;
	
	public SummaryData(CompiledData compiledData, MatchData[] _matchData) {
		
		this(compiledData.getEventID(), compiledData.getMatchesPlayed(), 
				compiledData.getRobot(), compiledData.getMatchComments(), 
				compiledData.getCompiledMatchData(), 
				compiledData.getCompiledDriverData(), _matchData);
	}

	public SummaryData(int _event, int[] _matchesPlayed, Robot _robot,
			String[] _matchComments, MetricValue[] _compiledMatchData,
			MetricValue[] _compiledDriverData, MatchData[] _matchData) {
		
		super(_event, _matchesPlayed, _robot, _matchComments, _compiledMatchData,
				_compiledDriverData);
		
		matchData = _matchData;
	}
	
	public MatchData[] getMatchData() {
		return matchData;
	}
}
