package com.team2052.frckrawler;

import android.app.Activity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.MatchData;
import com.team2052.frckrawler.database.structures.MetricValue;
import com.team2052.frckrawler.database.structures.Robot;
import com.team2052.frckrawler.gui.MetricWidget;

public class ScoutEditMatchDataActivity extends Activity implements OnClickListener {
	
	public static final String MATCH_ID_EXTRA = "com.tean2052.frckrawler.matchDataID";
	
	private DBManager dbManager;
	private MatchData matchData;
	private Robot[] robotChoices;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialogactivity_scout_edit_match_data);
		
		((Button)findViewById(R.id.cancel)).setOnClickListener(this);
		((Button)findViewById(R.id.remove)).setOnClickListener(this);
		((Button)findViewById(R.id.save)).setOnClickListener(this);
		((EditText)findViewById(R.id.matchNumber)).
				setInputType(InputType.TYPE_CLASS_NUMBER);
		
		dbManager = DBManager.getInstance(this);
		matchData = dbManager.scoutGetMatchData(getIntent().getIntExtra(MATCH_ID_EXTRA, -1));
		robotChoices = dbManager.scoutGetAllRobots();
		
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
			metricWidgetList.addView(MetricWidget.createWidget(this, 
					metrics[metricCount]));
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

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.save:
				
				finish();
				break;
				
			case R.id.remove:
				
				finish();
				break;
				
			case R.id.cancel:
				
				finish();
				break;
		}
	}
}