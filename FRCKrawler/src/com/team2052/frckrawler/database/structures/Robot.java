package com.team2052.frckrawler.database.structures;

public class Robot implements Structure {
	
	private int team;
	private int id;
	private String game;
	private String comments;
	private String imagePath;
	private MetricValue[] vals;
	
	public Robot(int _team, String _game, String _comments) {
		this(_team, _game, _comments, new MetricValue[0]);
	}
	
	public Robot(int _team, String _game, String _comments, MetricValue[] _vals) {
		this(_team, -1 , _game, _comments, null, _vals);
	}
	
	public Robot(int _team, int _id, String _game, String _comments, 
			String _imagePath, MetricValue[] _vals) {
		
		team = _team;
		id = _id;
		game = _game;
		comments = _comments;
		imagePath = _imagePath;
		vals = _vals;
	}
	
	public int getTeamNumber() {
		return team;
	}
	
	public int getID() {
		return id;
	}
	
	public String getGame() {
		return game;
	}
	
	public String getComments() {
		return comments;
	}
	
	public MetricValue[] getMetricValues() {
		return vals;
	}
	
	public Metric[] getMetrics() {
		
		Metric[] metrics = new Metric[vals.length];
		
		for(int i = 0; i < vals.length; i++)
			metrics[i] = vals[i].getMetric();
		
		return metrics;
	}
	
	public String getImagePath() {
		return imagePath;
	}
}
