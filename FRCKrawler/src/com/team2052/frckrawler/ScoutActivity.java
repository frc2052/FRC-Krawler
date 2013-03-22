package com.team2052.frckrawler;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.team2052.frckrawler.database.DBContract;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.MatchData;
import com.team2052.frckrawler.database.structures.Metric;
import com.team2052.frckrawler.database.structures.MetricValue;
import com.team2052.frckrawler.database.structures.Robot;
import com.team2052.frckrawler.gui.MetricWidget;
import com.team2052.frckrawler.gui.MyTextView;

public class ScoutActivity extends Activity implements OnClickListener, 
														OnItemSelectedListener {
	
	public static final int SCOUT_TYPE_MATCH = 1;
	public static final int SCOUT_TYPE_PIT = 2;
	public static final int SCOUT_TYPE_DRIVER = 3;
	
	public static final String SCOUT_TYPE_EXTRA = 
			"com.team2052.frckrawler.scoutTypeExtra";
	
	private DBManager dbManager;
	private volatile String[] teamNames;
	private volatile Robot[] robots;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scouting);
		
		findViewById(R.id.save).setOnClickListener(this);
		((Spinner)findViewById(R.id.teamNumber)).setOnItemSelectedListener(this);
		
		TextView title = (TextView)findViewById(R.id.scoutingType);
		switch(getIntent().getIntExtra(SCOUT_TYPE_EXTRA, 1)) {
			case SCOUT_TYPE_MATCH:
				title.setText("Match Scouting");
				break;
				
			case SCOUT_TYPE_PIT:
				title.setText("Pit Scouting");
				findViewById(R.id.matchNumber).setEnabled(false);
				break;
				
			case SCOUT_TYPE_DRIVER:
				title.setText("Driver Scouting");
				findViewById(R.id.matchNumber).setEnabled(false);
				break;
		}
		
		dbManager = DBManager.getInstance(this);
		
		new GetRobotListTask().execute();
		new GetMetricsTask().execute(getIntent().getIntExtra(SCOUT_TYPE_EXTRA, -1));
	}
	

	@Override
	public void onItemSelected(AdapterView<?> adapter, View spinner, int index,
			long id) {
		((TextView)findViewById(R.id.teamName)).setText(teamNames[index]);
	}

	@Override
	public void onNothingSelected(AdapterView<?> adapter) {}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.save) {
			
			int matchNumber = 0;
			int teamNumber = 0;
			
			try {
				if(getIntent().getIntExtra(SCOUT_TYPE_EXTRA, -1) 
						== SCOUT_TYPE_MATCH) {
					matchNumber = Integer.parseInt(((EditText)findViewById
						(R.id.matchNumber)).getText().toString());
				}
				
				teamNumber = Integer.parseInt(((Spinner)findViewById
						(R.id.teamNumber)).getSelectedItem().toString());
			} catch(NumberFormatException e) {
				Toast.makeText(this, "Cannot save data. You must specify a " +
						"team number and match number.", Toast.LENGTH_LONG).show();
				
				return;
			}
			
			switch(getIntent().getIntExtra(SCOUT_TYPE_EXTRA, -1)) {
				case SCOUT_TYPE_MATCH:
					
					LinearLayout metricList = 
						(LinearLayout)findViewById(R.id.metricWidgetList);
					MetricValue[] metricVals = 
							new MetricValue[metricList.getChildCount()];
					for(int i = 0; i < metricList.getChildCount(); i++) {
						MetricWidget widget = (MetricWidget)metricList.getChildAt(i);
						metricVals[i] = widget.getMetricValue();
					}
					
					MatchData data = new MatchData(
							dbManager.scoutGetEvent().getEventID(),
							Integer.parseInt(((EditText)findViewById(R.id.matchNumber)).getText().toString()),
							robots[((Spinner)findViewById(R.id.teamNumber)).getSelectedItemPosition()].getID(),
							-1,
							((Spinner)findViewById(R.id.gameType)).getSelectedItem().toString(),
							((EditText)findViewById(R.id.comments)).getText().toString(),
							metricVals
							);
					
					new SaveMatchDataTask().execute(data);
					
					break;
					
				case SCOUT_TYPE_PIT:
					
					break;
			}
		}
	}
	
	
	/*****
	 * Class: GetRobotListTask
	 * 
	 * @author Charles Hofer
	 *
	 * Summary: Executing this gets the robots from the database
	 * and puts their numbers in the number Spinner
	 */
	
	private class GetRobotListTask extends AsyncTask<Void, Void, Robot[]> {

		protected Robot[] doInBackground(Void... arg0) {
			teamNames = dbManager.scoutGetAllTeamNames();
			robots = dbManager.scoutGetAllRobots();
			return robots;
		}
		
		protected void onPostExecute(Robot[] robots) {
			
			String[] teamNumbers = new String[robots.length];
			for(int i = 0; i < robots.length; i++)
				teamNumbers[i] = Integer.toString(robots[i].getTeamNumber());
			
			ArrayAdapter<String> teamChoices = new ArrayAdapter<String>(
					getApplicationContext(), android.R.layout.simple_spinner_item,
					teamNumbers);
			
			((Spinner)findViewById(R.id.teamNumber)).setAdapter(teamChoices);
			
			if(teamNames.length > 0)
				((TextView)findViewById(R.id.teamName)).setText(teamNames[0]);
		}
	}
	
	
	/*****
	 * Class: GetMetricsTask
	 * 
	 * @author Charles Hofer
	 *
	 * Summary: when executed, puts the proper metric widgets
	 * in the list of metrics. Depends on what scout type
	 * is put in for the Integer param.
	 */
	
	private class GetMetricsTask extends AsyncTask<Integer, Void, Metric[]> {

		protected Metric[] doInBackground(Integer... params) {
			int scoutType = params[0];
			Metric[] metrics = new Metric[0];
			
			switch(scoutType) {
				case SCOUT_TYPE_MATCH:
					metrics = dbManager.scoutGetAllMatchMetrics();
					break;
					
				case SCOUT_TYPE_PIT:
					metrics = dbManager.scoutGetAllRobotMetrics();
					break;
					
				case SCOUT_TYPE_DRIVER:
					//metrics = dbManager.scoutGetAllDriverMetrics();
					break;
			}
			
			return metrics;
		}
		
		protected void onPostExecute(Metric[] metrics) {
			LinearLayout metricList = 
					(LinearLayout)findViewById(R.id.metricWidgetList);
			metricList.removeAllViews();
			
			for(Metric m : metrics)
				metricList.addView(MetricWidget.creatWidget
						(getApplicationContext(), m));
		}
	}
	
	
	/*****
	 * Class: SaveDataTask
	 * 
	 * Summary: saves the data depending on the scout type, and then
	 * clears the UI
	 */
	
	private class SaveMatchDataTask extends AsyncTask<MatchData, Void, Void> {

		@Override
		protected Void doInBackground(MatchData... params) {
			
			dbManager.scoutInsertMatchData(params[0]);
			dbManager.printQuery("SELECT * FROM " + DBContract.SCOUT_TABLE_MATCH_PERF, null);
			
			return null;
		}
		
		protected void onPostExecute(Void v) {
			
			Toast.makeText(getApplicationContext(), "Save successful.", 
					Toast.LENGTH_SHORT).show();
		}
	}
	
	
	//private class SaveRobotDataTask extends AsyncTask<>
}
