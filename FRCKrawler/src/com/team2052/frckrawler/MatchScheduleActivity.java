package com.team2052.frckrawler;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.team2052.frckrawler.database.DBContract;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.Match;
import com.team2052.frckrawler.database.structures.Robot;
import com.team2052.frckrawler.database.structures.Schedule;
import com.team2052.frckrawler.gui.MyButton;
import com.team2052.frckrawler.gui.MyTableRow;
import com.team2052.frckrawler.gui.MyTextView;
import com.team2052.frckrawler.gui.ProgressSpinner;
import com.team2052.frckrawler.gui.StaticTableLayout;

public class MatchScheduleActivity extends StackableTabActivity implements OnClickListener {
	private static int ADD_MATCH_REQUEST = 1;
	private static int REMOVE_MATCH_ID = 1;
	public static String EVENT_ID_EXTRA = "com.team2052.frckrawler.eventIDExtra";
	private int eventID;
	private DBManager db;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_schedule);
		eventID = Integer.parseInt(getIntent().getStringExtra(EVENT_ID_EXTRA));
		db = DBManager.getInstance(this);
		((Button)findViewById(R.id.addMatch)).setOnClickListener(this);
		new GetScheduleTask().execute();
	}
	
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.addMatch) {
			Intent i = new Intent(this, AddMatchDialogActivity.class);
			i.putExtra(EVENT_ID_EXTRA, eventID);
			startActivityForResult(i, ADD_MATCH_REQUEST);
		} else if(v.getId() == REMOVE_MATCH_ID) {
			db.removeMatch(eventID, (Integer)v.getTag());
			new GetScheduleTask().execute();
		}
	}
	
	@Override
	protected void onActivityResult(int request, int result, Intent data) {
		if(RESULT_OK == result) {
			new GetScheduleTask().execute();
		}
	}
	
	
	/*****
	 * Class: GetScheduleTask
	 * 
	 * Description: execute this task to get put matches into the data table
	 *****/
	private class GetScheduleTask extends AsyncTask<Void, TableRow, Void> {
		private int matchCount;
		private StaticTableLayout dataTable;
		
		@Override
		protected void onPreExecute() {
			((FrameLayout)findViewById(R.id.progressFrame)).
				addView(new ProgressSpinner(getApplicationContext()));
			matchCount = -1;
			dataTable = (StaticTableLayout)findViewById(R.id.scheduleTable);
			dataTable.removeAllViews();
		}
		
		@Override
		protected Void doInBackground(Void... v) {
			Schedule schedule = db.getSchedule(eventID);
			MyTableRow staticDesRow = new MyTableRow(
					MatchScheduleActivity.this,
					new View[] {
							new MyTextView(MatchScheduleActivity.this, "", 18),
							new MyTextView(MatchScheduleActivity.this, "Match #", 18)
					});
			MyTableRow desRow = new MyTableRow(
					MatchScheduleActivity.this,
					new View[] {
							new MyTextView(MatchScheduleActivity.this, "Red 1", 18),
							new MyTextView(MatchScheduleActivity.this, "Red 2", 18),
							new MyTextView(MatchScheduleActivity.this, "Red 3", 18),
							new MyTextView(MatchScheduleActivity.this, "Blue 1", 18),
							new MyTextView(MatchScheduleActivity.this, "Blue 2", 18),
							new MyTextView(MatchScheduleActivity.this, "Blue 3", 18),
							new MyTextView(MatchScheduleActivity.this, "Red Score", 18),
							new MyTextView(MatchScheduleActivity.this, "Blue Score", 18)
					});
			desRow.setLayoutParams(new TableLayout.LayoutParams());
			publishProgress(staticDesRow, desRow);
			for(int i = 0; i < schedule.getNumberMatches(); i++) {
				int color;
				if(i % 2 == 0)
					color = GlobalValues.ROW_COLOR;
				else
					color = Color.TRANSPARENT;
				Match match = schedule.getAllMatches()[i];
				String red1 = getRobotTeamNum(match.getRed1RobotID());
				String red2 = getRobotTeamNum(match.getRed2RobotID());
				String red3 = getRobotTeamNum(match.getRed3RobotID());
				String blue1 = getRobotTeamNum(match.getBlue1RobotID());
				String blue2 = getRobotTeamNum(match.getBlue2RobotID());
				String blue3 = getRobotTeamNum(match.getBlue3RobotID());
				String redScore = Integer.toString(match.getRedScore());
				if(-1 == match.getRedScore())
					redScore = " ";
				String blueScore = Integer.toString(match.getBlueScore());
				if(-1 == match.getBlueScore())
					blueScore = " ";
				MyButton remButton = new MyButton(MatchScheduleActivity.this,
						"Remove", MatchScheduleActivity.this);
				remButton.setId(REMOVE_MATCH_ID);
				remButton.setTag(match.getMatchNumber());
				MyTableRow statRow = new MyTableRow(
						MatchScheduleActivity.this,
						new View[] {
								remButton,
								new MyTextView(MatchScheduleActivity.this, 
										Integer.toString(match.getMatchNumber()), 18)
						}, color);
				MyTableRow mainRow = new MyTableRow(
						MatchScheduleActivity.this,
						new View[] {
								new MyTextView(MatchScheduleActivity.this, red1, 18),
								new MyTextView(MatchScheduleActivity.this, red2, 18),
								new MyTextView(MatchScheduleActivity.this, red3, 18),
								new MyTextView(MatchScheduleActivity.this, blue1, 18),
								new MyTextView(MatchScheduleActivity.this, blue2, 18),
								new MyTextView(MatchScheduleActivity.this, blue3, 18),
								new MyTextView(MatchScheduleActivity.this, redScore, 18),
								new MyTextView(MatchScheduleActivity.this, blueScore, 18)
						}, color);
				publishProgress(statRow, mainRow);
			}
			return null;
		}
		
		@Override
		protected void onProgressUpdate(TableRow... rows) {
			dataTable.addViewToStaticTable(rows[0]);
			dataTable.addViewToMainTable(rows[1]);
			matchCount++;
		}
		
		@Override
		protected void onPostExecute(Void v) {
			((TextView)findViewById(R.id.matchCount)).setText(matchCount + " Matches");
				((FrameLayout)findViewById(R.id.progressFrame)).removeAllViews();
		}
		
		@Override
		protected void onCancelled(Void v) {
			((TextView)findViewById(R.id.matchCount)).setText(matchCount + " Matches");
			((FrameLayout)findViewById(R.id.progressFrame)).removeAllViews();
		}
		
		private String getRobotTeamNum(int robotID) {
			Robot[] robots = db.getRobotsByColumns(
					new String[] {DBContract.COL_ROBOT_ID}, 
					new String[] {Integer.toString(robotID)});
			if(robots != null && robots.length > 0) {
				return Integer.toString(robots[0].getTeamNumber());
			}
			return "";
		}
	}
}
