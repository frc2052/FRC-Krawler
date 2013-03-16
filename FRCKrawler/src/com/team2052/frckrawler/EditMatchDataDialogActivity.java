package com.team2052.frckrawler;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.database.DBContract;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.MatchData;
import com.team2052.frckrawler.database.structures.MetricValue;
import com.team2052.frckrawler.database.structures.Robot;
import com.team2052.frckrawler.gui.MetricWidget;


public class EditMatchDataDialogActivity extends Activity implements OnClickListener, 
											android.content.DialogInterface.OnClickListener {
	
	public static final String GAME_NAME_EXTRA = "com.team2052.frckrawler.gameNameExtra";
	public static final String EVENT_ID_EXTRA = "com.team2052.frckrawler.eventIDExtra";
	public static final String MATCH_ID_EXTRA = "com.team2052.frckrawler.matchIDExtra";
	
	private DBManager db;
	private Robot[] robotChoices;
	
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialogactivity_edit_match_data);
		
		findViewById(R.id.save).setOnClickListener(this);
		findViewById(R.id.cancel).setOnClickListener(this);
		findViewById(R.id.remove).setOnClickListener(this);
		
		db = DBManager.getInstance(this);
	}
	
	public void onResume() {
		
		super.onResume();
		
		System.out.println(getIntent().getIntExtra(MATCH_ID_EXTRA, -1));
		
		//Get the match data to edit from the database
		MatchData[] arr = db.getMatchDataByColumns(new String[] {DBContract.COL_DATA_ID}, 
				new String[] {Integer.toString(getIntent().getIntExtra(MATCH_ID_EXTRA, -1))});
		MatchData matchData;
		
		if(arr.length > 0)
			matchData = arr[0];
		else
			return;
		
		//Add the list of robots to the robot spinner and set the proper one selected
		robotChoices = db.getRobotsAtEvent
				(getIntent().getIntExtra(EVENT_ID_EXTRA, -1));
		String[] robotTeams = new String[robotChoices.length];
		
		int selectedRobotPos = 0;
				
		for(int i = 0; i < robotChoices.length; i++) {
			robotTeams[i] = Integer.toString(robotChoices[i].getTeamNumber());
			
			if(robotChoices[i].getID() == matchData.getRobotID())
				selectedRobotPos = i;
		}
				
		Spinner robotSpinner = (Spinner)findViewById(R.id.robot);
				
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
				android.R.layout.simple_spinner_item, robotTeams);
				
		robotSpinner.setAdapter(adapter);
		robotSpinner.setSelection(selectedRobotPos);
				
		//Add the metrics to the UI
		MetricValue[] metrics = matchData.getMetricValues();
				
		LinearLayout metricWidgetList = (LinearLayout)findViewById(R.id.metricWidgetList);
		metricWidgetList.removeAllViews();
				
		for(int metricCount = 0; metricCount < metrics.length; metricCount++) {
			metricWidgetList.addView(MetricWidget.createWidget(this, metrics[metricCount]));
		}
		
		//Set the comments
		((EditText)findViewById(R.id.comments)).setText(matchData.getComments());
		
		//Set the match number
		((EditText)findViewById(R.id.matchNumber)).
				setText(Integer.toString(matchData.getMatchNumber()));
		
		//Set the match type
		int selectedMatchTypePos = 0;
		String[] matchTypes = getResources().getStringArray(R.array.match_types);
		
		for(int i = 0; i < matchTypes.length; i++)
			if(matchData.getMatchType().equals(matchTypes[i])) {
				selectedMatchTypePos = i;
				break;
			}
		
		((Spinner)findViewById(R.id.gameType)).setSelection(selectedMatchTypePos);
	}

	public void onClick(View v) {
		
		if(v.getId() == R.id.save) {
			
			try {
				LinearLayout metricList = (LinearLayout)findViewById(R.id.metricWidgetList);
				MetricValue[] metricVals = new MetricValue[metricList.getChildCount()];
			
				for(int widgetCount = 0; widgetCount < metricList.getChildCount(); widgetCount++) {
				
					MetricWidget widget = (MetricWidget)metricList.getChildAt(widgetCount);
					metricVals[widgetCount] = widget.getMetricValue();
				}
				
				String[] updateCols = new String[metricVals.length + 4];
				String[] updateVals = new String[metricVals.length + 4];
				
				updateCols[0] = DBContract.COL_ROBOT_ID;
				updateVals[0] = Integer.toString
						(db.getRobotsByColumns(new String[] {DBContract.COL_TEAM_NUMBER}, 
								new String[] {((Spinner)findViewById(R.id.robot)).
									getSelectedItem().toString()})[0].getID());
				
				updateCols[1] = DBContract.COL_MATCH_NUMBER;
				updateVals[1] = ((EditText)findViewById(R.id.matchNumber)).getText().toString();
				
				updateCols[2] = DBContract.COL_MATCH_TYPE;
				updateVals[2] = ((Spinner)findViewById(R.id.gameType)).getSelectedItem().toString();
				
				updateCols[3] = DBContract.COL_COMMENTS;
				updateVals[3] = ((EditText)findViewById(R.id.comments)).getText().toString();
				
				for(int i = 0; i < metricVals.length; i++) {
					
					updateCols[i + 4] = metricVals[i].getMetric().getKey();
					updateVals[i + 4] = metricVals[i].getValueAsDBReadableString();
				}
			
				db.updateMatchData(new String[] {DBContract.COL_DATA_ID}, 
						new String[] {Integer.toString(getIntent().
								getIntExtra(MATCH_ID_EXTRA, -1))}, 
						updateCols, updateVals);
				
			
				finish();
				
			} catch(NumberFormatException e) {
				
				Toast.makeText(this, "Data not added to the database. You must specify " +
						"a match number.", Toast.LENGTH_SHORT).show();
			}
			
		} else if(v.getId() == R.id.remove) {
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			
			builder.setTitle("Remove This Entry?");
			builder.setMessage("Are you sure you want to remove this set of match data from " +
					"the database? It will be cast into the cold void of cyberspace for" +
					" eternity.");
			builder.setNegativeButton("No", this);
			builder.setPositiveButton("Yes", this);
			
			builder.show();
			
		} else if(v.getId() == R.id.cancel) {
			
			finish();
		}
	}

	public void onClick(DialogInterface dialog, int which) {
		
		if(which == DialogInterface.BUTTON_POSITIVE) {
			
			db.removeMatchData(getIntent().getIntExtra(EVENT_ID_EXTRA, -1));
			finish();
			
		} else {
			
			dialog.dismiss();
		}
	}
}
