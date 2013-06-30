package com.team2052.frckrawler;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
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
import com.team2052.frckrawler.database.structures.MetricValue.MetricTypeMismatchException;
import com.team2052.frckrawler.database.structures.Robot;
import com.team2052.frckrawler.database.structures.User;
import com.team2052.frckrawler.gui.MetricWidget;

public class ScoutActivity extends Activity implements OnClickListener, 
														OnItemSelectedListener,
													DialogInterface.OnClickListener{
	
	public static final int SCOUT_TYPE_MATCH = 1;
	public static final int SCOUT_TYPE_PIT = 2;
	public static final int SCOUT_TYPE_DRIVER = 3;
	
	public static final String SCOUT_TYPE_EXTRA = 
			"com.team2052.frckrawler.scoutTypeExtra";
	
	private DBManager dbManager;
	private volatile String[] teamNames;
	private volatile Robot[] robots;
	private volatile User user;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode
			(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setContentView(R.layout.activity_scouting);
		
		findViewById(R.id.save).setOnClickListener(this);
		((Spinner)findViewById(R.id.teamNumber)).setOnItemSelectedListener(this);
		
		ArrayList<String> matchTypes = new ArrayList<String>();
		matchTypes.add("Qualifications");
		matchTypes.add("Finals");
		matchTypes.add("Practice");
		ArrayAdapter<String> matchAdapter = new ArrayAdapter<String>
				(this, R.layout.scout_spinner_item, matchTypes);
		((Spinner)findViewById(R.id.gameType)).setAdapter(matchAdapter);
		
		TextView title = (TextView)findViewById(R.id.scoutingType);
		switch(getIntent().getIntExtra(SCOUT_TYPE_EXTRA, 1)) {
			case SCOUT_TYPE_MATCH:
				title.setText("Match Scouting");
				break;
				
			case SCOUT_TYPE_PIT:
				title.setText("Pit Scouting");
				findViewById(R.id.matchNumber).setEnabled(false);
				findViewById(R.id.gameType).setEnabled(false);
				break;
				
			case SCOUT_TYPE_DRIVER:
				title.setText("Driver Scouting");
				findViewById(R.id.matchNumber).setEnabled(false);
				findViewById(R.id.gameType).setEnabled(false);
				break;
		}
		
		dbManager = DBManager.getInstance(this);
		User[] allUsers = dbManager.scoutGetAllUsers();
		for(User u : allUsers)
			if(GlobalSettings.userID == u.getID())
				user = u;
		
		new GetRobotListTask().execute();
	}
	
	public void resetUI() {
		new GetMetricsTask().execute(((Spinner)findViewById(R.id.teamNumber)).
				getSelectedItemPosition());
		
		((EditText)findViewById(R.id.comments)).setText("");
		((EditText)findViewById(R.id.matchNumber)).setText("");
		
		try {
			((Spinner)findViewById(R.id.teamNumber)).setSelection(0);
		} catch(IndexOutOfBoundsException e) {}
	}

	@Override
	public void onItemSelected(AdapterView<?> adapter, View spinner, int index,
			long id) {
		((TextView)findViewById(R.id.teamName)).setText(teamNames[index]);
		
		if(getIntent().getIntExtra(SCOUT_TYPE_EXTRA, -1) == SCOUT_TYPE_PIT) {
			if(robots[index].getComments() == null || 
					robots[index].getComments().replace(" ", "").equals("")) {
				((EditText)findViewById(R.id.comments)).
					setText("");
				
			} else {
				((EditText)findViewById(R.id.comments)).
						setText(robots[index].getComments());
			}
		
			new GetMetricsTask().execute(((Spinner)findViewById(R.id.teamNumber)).
					getSelectedItemPosition());
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> adapter) {}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.save) {
			if(robots.length < 1) {
				Toast.makeText(this, "Could not save. No robots at " +
						"this competition.", Toast.LENGTH_LONG).show();

			/*} else if(((EditText)findViewById(R.id.matchNumber)).getText().equals("") || 
					((EditText)findViewById(R.id.matchNumber)).getText() == null) {
				Toast.makeText(this, "Could not save. No match number " +
						"specified.", Toast.LENGTH_LONG).show();*/

			} else {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Save");
				builder.setMessage("Are you sure you want to save the entered data? " +
						"You can review or edit it later with in match data.");
				builder.setPositiveButton("Yes", this);
				builder.setNegativeButton("No", this);
				builder.show();
			}
		}
	}
	
	
	@Override
	public void onClick(DialogInterface dialog, int which) {
		if(which == DialogInterface.BUTTON_POSITIVE) {
			
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
			
			//Compile the entered data
			LinearLayout metricList = 
					(LinearLayout)findViewById(R.id.metricWidgetList);
			MetricValue[] metricVals = 
					new MetricValue[metricList.getChildCount()];
			for(int i = 0; i < metricList.getChildCount(); i++) {
				MetricWidget widget = (MetricWidget)metricList.getChildAt(i);
				metricVals[i] = widget.getMetricValue();
			}
			
			switch(getIntent().getIntExtra(SCOUT_TYPE_EXTRA, -1)) {
				case SCOUT_TYPE_MATCH:
					
					MatchData data = new MatchData(
							dbManager.scoutGetEvent().getEventID(),
							matchNumber,
							robots[((Spinner)findViewById(R.id.teamNumber)).
							       getSelectedItemPosition()].getID(),
							user.getID(),
							((Spinner)findViewById(R.id.gameType)).
									getSelectedItem().toString(),
							((EditText)findViewById(R.id.comments)).getText().toString(),
							metricVals
							);
					
					new SaveMatchDataTask().execute(data);
					
					break;
					
				case SCOUT_TYPE_PIT:
					
					int selectedRobot = ((Spinner)findViewById(R.id.teamNumber)).
							getSelectedItemPosition();
					
					Robot robot = new Robot(
							teamNumber,
							robots[selectedRobot].getID(),
							robots[selectedRobot].getGame(),
							((EditText)findViewById(R.id.comments)).
									getText().toString(),
							robots[selectedRobot].getImagePath(),
							metricVals
							);
					
					new SaveRobotDataTask().execute(robot);
					
					break;
			}
		}
		
		dialog.dismiss();
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
					getApplicationContext(), R.layout.scout_spinner_item,
					teamNumbers);
			((Spinner)findViewById(R.id.teamNumber)).setAdapter(teamChoices);
			
			if(teamNames.length > 0)
				((TextView)findViewById(R.id.teamName)).setText(teamNames[0]);
			
			resetUI();
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
	
	private class GetMetricsTask extends AsyncTask<Integer, Void, MetricValue[]> {

		protected MetricValue[] doInBackground(Integer... params) {
			int selectedRobot = params[0];
			MetricValue[] metrics = new MetricValue[0];
			
			switch(getIntent().getIntExtra(SCOUT_TYPE_EXTRA, -1)) {
				case SCOUT_TYPE_MATCH:
					Metric[] rawMetrics = dbManager.scoutGetAllMatchMetrics();
					metrics = new MetricValue[rawMetrics.length];
					
					for(int i = 0; i < rawMetrics.length; i++) {
						try {
							metrics[i] = new MetricValue
									(rawMetrics[i], new String[0]);
						} catch(MetricTypeMismatchException e) {
							Log.e("FRCKrawler", "Metric mismatch exception.");
						}
					}
					
					break;
					
				case SCOUT_TYPE_PIT:
					if(robots.length > 0 && selectedRobot > -1)
						metrics = robots[selectedRobot].getMetricValues();
					break;
					
				case SCOUT_TYPE_DRIVER:
					//metrics = dbManager.scoutGetAllDriverMetrics();
					break;
			}
			
			return metrics;
		}
		
		protected void onPostExecute(MetricValue[] metrics) {
			LinearLayout metricList = 
					(LinearLayout)findViewById(R.id.metricWidgetList);
			metricList.removeAllViews();
			
			for(MetricValue m : metrics)
				metricList.addView(MetricWidget.createWidget
						(getApplicationContext(), m));
		}
	}
	
	
	/*****
	 * Class: SaveMatchDataTask
	 * 
	 * Summary: saves the match data and then
	 * clears the UI
	 */
	
	private class SaveMatchDataTask extends AsyncTask<MatchData, Void, Void> {

		@Override
		protected Void doInBackground(MatchData... params) {
			
			dbManager.scoutInsertMatchData(params[0]);
			dbManager.printQuery("SELECT * FROM " + DBContract.SCOUT_TABLE_MATCH_PERF, null);
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void v) {
			resetUI();
			Toast.makeText(getApplicationContext(), "Save successful.", 
					Toast.LENGTH_SHORT).show();
		}
	}
	
	
	/*****
	 * Class: SaveRobotDataTask
	 * 
	 * @author Charles Hofer
	 *
	 * Summary: Updates the specified robot in the database.
	 */
	
	private class SaveRobotDataTask extends AsyncTask<Robot, Void, Void> {

		@Override
		protected Void doInBackground(Robot... params) {
			dbManager.scoutUpdateRobot(params[0]);
			
			if(getIntent().getIntExtra(SCOUT_TYPE_EXTRA, -1) == SCOUT_TYPE_PIT) {
				robots = dbManager.scoutGetAllRobots();
			}
			
			return null;
		}
		
		protected void onPostExecute(Void v) {
			resetUI();
			Toast.makeText(getApplicationContext(), "Save successful.", 
					Toast.LENGTH_SHORT).show();
		}
	}
}
