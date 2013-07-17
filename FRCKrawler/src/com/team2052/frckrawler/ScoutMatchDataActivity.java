package com.team2052.frckrawler;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TableLayout;

import com.team2052.frckrawler.database.DBContract;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.MatchData;
import com.team2052.frckrawler.database.structures.MetricValue;
import com.team2052.frckrawler.database.structures.Robot;
import com.team2052.frckrawler.database.structures.User;
import com.team2052.frckrawler.gui.MyButton;
import com.team2052.frckrawler.gui.MyTableRow;
import com.team2052.frckrawler.gui.MyTextView;

public class ScoutMatchDataActivity extends Activity implements OnClickListener {
	
	private static final int EDIT_BUTTON_ID = 1;
	
	private DBManager dbManager;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scout_match_data);
		
		dbManager = DBManager.getInstance(this);
		new GetMatchDataTask().execute();
	}
	
	@Override
	public void onClick(View v) {
		Intent i = new Intent(this, ScoutEditMatchDataActivity.class);
		i.putExtra(ScoutEditMatchDataActivity.MATCH_ID_EXTRA, ((Integer)v.getTag()).intValue());
		startActivityForResult(i, ScoutEditMatchDataActivity.REQUEST_CODE);
	}
	
	@Override
	protected void onActivityResult (int requestCode, int resultCode, Intent data) {
		if(requestCode == ScoutEditMatchDataActivity.REQUEST_CODE) {
			if(resultCode == ScoutEditMatchDataActivity.RESULT_REFRESH)
				new GetMatchDataTask().execute();
		}
	}
	
	private class GetMatchDataTask extends AsyncTask<Void, MyTableRow, Void> {
		
		private TableLayout dataTable;
		
		@Override
		protected void onPreExecute() {
			dataTable = (TableLayout)findViewById(R.id.dataTable);
			dataTable.removeAllViews();
		}

		@Override
		protected Void doInBackground(Void... v) {
			User[] users = dbManager.scoutGetAllUsers();
			Robot[] robots = dbManager.scoutGetAllRobots();
			MatchData[] matchData = dbManager.scoutGetAllMatchData();
			
			MyTableRow descriptorsRow = new MyTableRow(ScoutMatchDataActivity.this);
			descriptorsRow.addView(new MyTextView(ScoutMatchDataActivity.this, " ", 18));
			descriptorsRow.addView(new MyTextView(ScoutMatchDataActivity.this, "Team", 18));
			descriptorsRow.addView(new MyTextView(ScoutMatchDataActivity.this, "Match #", 18));
			descriptorsRow.addView(new MyTextView(ScoutMatchDataActivity.this, "User", 18));
			descriptorsRow.addView(new MyTextView(ScoutMatchDataActivity.this, "Match Type", 18));
			descriptorsRow.addView(new MyTextView(ScoutMatchDataActivity.this, "Comments", 18));
			
			if(matchData.length > 0) {
				for(MetricValue val : matchData[0].getMetricValues())
					if(val.getMetric().getType() != DBContract.MATH)
						descriptorsRow.addView(new MyTextView
								(ScoutMatchDataActivity.this, 
										val.getMetric().getMetricName(), 18));
			}
			
			publishProgress(descriptorsRow);
			
			for(int i = 0; i < matchData.length; i++) {
				int color;
				
				if(i % 2 == 0)
					color = GlobalValues.ROW_COLOR;
				else
					color = Color.TRANSPARENT;
				
				MyTableRow row = new MyTableRow(ScoutMatchDataActivity.this);
				row.setBackgroundColor(color);
				
				MyButton editButton = new MyButton(ScoutMatchDataActivity.this, "Edit Data", 
						ScoutMatchDataActivity.this);
				editButton.setId(EDIT_BUTTON_ID);
				editButton.setTag(matchData[i].getMatchID());
				row.addView(editButton);
				
				for(int k = 0; k < robots.length; k++) {
					if(robots[k].getID() == matchData[i].getRobotID()) {
						row.addView(new MyTextView(ScoutMatchDataActivity.this, 
								Integer.toString(robots[k].getTeamNumber()), 18));
						break;
					}
				}
				
				row.addView(new MyTextView(ScoutMatchDataActivity.this, 
						Integer.toString(matchData[i].getMatchNumber()), 18));
				
				for(int k = 0; k < users.length; k++) {
					if(users[k].getID() == matchData[i].getUserID()) {
						row.addView(new MyTextView(ScoutMatchDataActivity.this,
								users[k].getName(), 18));
						break;
					}
				}
				
				row.addView(new MyTextView(ScoutMatchDataActivity.this, 
						matchData[i].getMatchType(), 18));
				row.addView(new MyTextView(ScoutMatchDataActivity.this, 
						matchData[i].getComments(), 18));
				
				for(int k = 0; k < matchData[i].getMetricValues().length; k++) {
					if(matchData[i].getMetricValues()[k].getMetric().getType() != 
							DBContract.MATH) {
						row.addView(new MyTextView(ScoutMatchDataActivity.this, 
								matchData[i].getMetricValues()[k].
								getValueAsHumanReadableString(), 18));
					}
				}
				
				publishProgress(row);
			}
			
			return null;
		}
		
		@Override
		protected void onProgressUpdate(MyTableRow... rows) {
			dataTable.addView(rows[0]);
		}
	}
}
