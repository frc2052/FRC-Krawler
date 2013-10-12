package com.team2052.frckrawler.database.structures;

public class Robot implements Structure {
	
	private boolean isChecked;	//Only used when getting robots by thier event
	private int team;
	private int id;
	private String game;
	private String comments;
	private String imagePath;
	private double opr;
	private MetricValue[] vals;
	
	public Robot(int _team, String _game, String _comments) {
		this(_team, _game, _comments, new MetricValue[0]);
	}
	
	public Robot(int _team, String _game, String _comments, MetricValue[] _vals) {
		this(_team, -1 , _game, _comments, null, -1, _vals);
	}
	
	public Robot(int _team, String _game, String _comments, double _opr, MetricValue[] _vals) {
		this(_team, -1 , _game, _comments, null, _opr, _vals);
	}
	
	public Robot(int _team, int _id, String _game, String _comments, 
			String _imagePath, double _opr, MetricValue[] _vals) {
		this(false, _team, _id, _game, _comments, _imagePath, _opr, _vals);
	}
	
	public Robot(boolean _isChecked, int _team, int _id, String _game, String _comments, 
			String _imagePath, double _opr, MetricValue[] _vals) {
		
		isChecked = _isChecked;
		team = _team;
		id = _id;
		game = _game;
		comments = _comments;
		imagePath = _imagePath;
		opr = _opr;
		vals = _vals;
	}
	
	/*****
	 * !WARNING!  This will not change the checked state of this
	 * robot in the entire database, only this object. It is
	 * not recommended that this method be used unless the 
	 * programmer is CERTAIN what he or she is doing  !WARNING!
	 */
	public void setChecked(boolean checked) {
		isChecked = checked;
	}
	
	public boolean isChecked() {
		return isChecked;
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
	
	public double getOPR() {
		return opr;
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
