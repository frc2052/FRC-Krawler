package com.team2052.frckrawler;

import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;

import com.team2052.frckrawler.database.DBContract;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.Event;
import com.team2052.frckrawler.database.structures.MatchData;
import com.team2052.frckrawler.database.structures.MetricValue;
import com.team2052.frckrawler.database.structures.Robot;
import com.team2052.frckrawler.database.structures.User;
import com.team2052.frckrawler.gui.MyButton;
import com.team2052.frckrawler.gui.MyTableRow;
import com.team2052.frckrawler.gui.MyTextView;
import com.team2052.frckrawler.gui.ProgressSpinner;

public class RawMatchDataActivity extends StackableTabActivity implements OnClickListener {
	
	private static final int COMMENT_CHAR_LIMIT = 20;
	private static final int EDIT_BUTTON_ID = 1;
	
	private DBManager dbManager;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_match_data);
		
		findViewById(R.id.addData).setOnClickListener(this);
		
		dbManager = DBManager.getInstance(this);
	}
	
	public void onResume() {
		super.onResume();
		
		((FrameLayout)findViewById(R.id.progressFrame)).
		addView(new ProgressSpinner(getApplicationContext()));
		
		new GetMatchDataTask().execute(this);
	}
	
	public void postResults(TableLayout table) {
		RelativeLayout tableParent = 
				(RelativeLayout)findViewById(R.id.dataTableParent);
		tableParent.removeAllViews();
		tableParent.addView(table);
		
		((FrameLayout)findViewById(R.id.progressFrame)).removeAllViews();
	}
	
	public void onClick(View v) {
		Intent i;
		
		Event e = dbManager.getEventsByColumns(new String[] {DBContract.COL_EVENT_ID}, 
				new String[] {databaseValues
				[getAddressOfDatabaseKey(DBContract.COL_EVENT_ID)]})[0];
		
		switch(v.getId()) {
			case R.id.addData:
				
				i = new Intent(this, AddMatchDataDialogActivity.class);
				i.putExtra(AddMatchDataDialogActivity.EVENT_ID_EXTRA, e.getEventID());
				i.putExtra(AddMatchDataDialogActivity.GAME_NAME_EXTRA, e.getGameName());
				startActivity(i);
				
				break;
				
			case EDIT_BUTTON_ID:
				
				i = new Intent(this, EditMatchDataDialogActivity.class);
				i.putExtra(EditMatchDataDialogActivity.EVENT_ID_EXTRA, e.getEventID());
				i.putExtra(EditMatchDataDialogActivity.GAME_NAME_EXTRA, e.getGameName());
				i.putExtra(EditMatchDataDialogActivity.MATCH_ID_EXTRA, (Integer)v.getTag());
				startActivity(i);
				
				System.out.println(v.getTag());
				
				break;
		}
	}
	
	private class GetMatchDataTask extends AsyncTask<RawMatchDataActivity, Void, TableLayout> {

		protected TableLayout doInBackground(RawMatchDataActivity... params) {
			
			RawMatchDataActivity activity = params[0];
			
			TableLayout table = new TableLayout(activity);
			MatchData[] matchData = dbManager.
					getMatchDataByColumns(databaseKeys, databaseValues);
			
			//Create the descriptors row and add it to the array
			MyTableRow descriptorsRow = new MyTableRow(activity);
			
			descriptorsRow.addView(new MyTextView(activity, " ", 18));
			descriptorsRow.addView(new MyTextView(activity, "Event", 18));
			descriptorsRow.addView(new MyTextView(activity, "Team", 18));
			descriptorsRow.addView(new MyTextView(activity, "Match #", 18));
			descriptorsRow.addView(new MyTextView(activity, "User", 18));
			descriptorsRow.addView(new MyTextView(activity, "Match Type", 18));
			descriptorsRow.addView(new MyTextView(activity, "Comments", 18));
			
			if(matchData.length > 0) {
				for(MetricValue v : matchData[0].getMetricValues()) {
					
					descriptorsRow.addView
						(new MyTextView(activity, v.getMetric().getMetricName(), 18));
				}
			}
			
			table.addView(descriptorsRow);
			
			//Add a row for every data entry
			for(int i = 0; i < matchData.length; i++) {
				
				int color;
				
				if(i % 2 == 0)
					color = GlobalSettings.ROW_COLOR;
				else
					color = Color.TRANSPARENT;
				
				
				//These calls to the database should take very little time, so they are 
				//"acceptable" to do without an AsyncTask
				Event[] eventArr = dbManager.getEventsByColumns
						(new String[] {DBContract.COL_EVENT_ID}, 
								new String[] {Integer.toString(matchData[i].getEventID())});
				
				Robot[] robotsArr = dbManager.getRobotsByColumns
						(new String[] {DBContract.COL_ROBOT_ID}, 
								new String[] {Integer.toString(matchData[i].getRobotID())});
				
				User[] userArr = dbManager.getUsersByColumns
						(new String[] {DBContract.COL_USER_ID}, 
								new String[] {Integer.toString(matchData[i].getUserID())});
				
				MyButton editButton = new MyButton(activity, "Edit Data", activity);
				editButton.setId(EDIT_BUTTON_ID);
				editButton.setTag(Integer.valueOf(matchData[i].getMatchID()));
				
				ArrayList<View> viewArr = new ArrayList<View>();
				
				viewArr.add(editButton);
				viewArr.add(new MyTextView(activity, eventArr[0].getEventName(), 18));
				viewArr.add(new MyTextView(activity, Integer.toString(robotsArr[0].getTeamNumber()), 
						18));
				viewArr.add(new MyTextView(activity, Integer.toString(matchData[i].getMatchNumber()), 
						18));
				if(userArr.length > 0)
					viewArr.add(new MyTextView(activity, userArr[0].getName(), 18));
				else 
					viewArr.add(new MyTextView(activity, " ", 18));
				viewArr.add(new MyTextView(activity, matchData[i].getMatchType(), 18));
				
				//Put a character limit on the comments string
				String displayedString = new String();
				
				if(matchData[i].getComments().length() < COMMENT_CHAR_LIMIT)
					displayedString = matchData[i].getComments();
				else
					displayedString = matchData[i].getComments().
						substring(0, COMMENT_CHAR_LIMIT - 1);
					
				viewArr.add(new MyTextView(activity, displayedString, 18));
				
				//Add the metric data
				MetricValue[] metricValues = matchData[i].getMetricValues();
				
				for(int k = 0; k < metricValues.length; k++) {
					viewArr.add(new MyTextView(activity, metricValues[k].
							getValueAsHumanReadableString(), 18));
				}
				
				table.addView(new MyTableRow(activity, viewArr.toArray
						(new View[0]), color));
			}
			
			return table;
		}
		
		protected void onPostExecute(TableLayout table) {
			
			postResults(table);
		}
	}
}
