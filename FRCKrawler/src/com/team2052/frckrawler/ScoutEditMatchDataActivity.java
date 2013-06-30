package com.team2052.frckrawler;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.team2052.frckrawler.database.DBContract;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.MatchData;
import com.team2052.frckrawler.database.structures.MetricValue;
import com.team2052.frckrawler.database.structures.Robot;
import com.team2052.frckrawler.gui.MetricWidget;

public class ScoutEditMatchDataActivity extends Activity implements OnClickListener {

	public static final int REQUEST_CODE = 1;
	public static final int RESULT_REFRESH = 2;
	public static final int RESULT_NO_REFRESH = 3;
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
		((EditText)findViewById(R.id.matchNumber)).setInputType(InputType.TYPE_CLASS_NUMBER);

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
			try {
				LinearLayout metricList = (LinearLayout)findViewById(R.id.metricWidgetList);
				MetricValue[] metricVals = new MetricValue[metricList.getChildCount()];

				for(int widgetCount = 0; widgetCount < metricList.getChildCount(); 
						widgetCount++) {
					MetricWidget widget = (MetricWidget)metricList.getChildAt(widgetCount);
					metricVals[widgetCount] = widget.getMetricValue();
				}

				String[] updateCols = new String[metricVals.length + 4];
				String[] updateVals = new String[metricVals.length + 4];

				updateCols[0] = DBContract.COL_ROBOT_ID;
				for(int i = 0; i < robotChoices.length; i++) {
					if(robotChoices[i].getTeamNumber() == 
							Integer.parseInt(((Spinner)findViewById(R.id.robot)).
									getSelectedItem().toString())) {
						updateVals[0] = Integer.toString(robotChoices[i].getID());
						break;
					}
				}

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

				dbManager.scoutUpdateMatchData(new String[] {DBContract.COL_DATA_ID}, 
						new String[] {Integer.toString(getIntent().
								getIntExtra(MATCH_ID_EXTRA, -1))}, 
								updateCols, updateVals);

				setResult(RESULT_REFRESH);
				finish();

			} catch(NumberFormatException e) {
				Toast.makeText(this, "Data not added to the database. You must specify " +
						"a match number.", Toast.LENGTH_SHORT).show();
			}

			break;

		case R.id.remove:

			dbManager.scoutRemoveMatchData(getIntent().getIntExtra(MATCH_ID_EXTRA, -1));
			setResult(RESULT_REFRESH);
			finish();
			break;

		case R.id.cancel:

			setResult(RESULT_NO_REFRESH);
			finish();
			break;
		}
	}
}