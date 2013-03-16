package com.team2052.frckrawler;

import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.example.frckrawler.R;
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

public class RawMatchDataActivity extends StackableTabActivity implements OnClickListener {
	
	private static final int COMMENT_CHAR_LIMIT = 20;
	private static final int EDIT_BUTTON_ID = 1;
	
	private DBManager dbManager;
	
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_match_data);
		
		findViewById(R.id.addData).setOnClickListener(this);
		
		dbManager = DBManager.getInstance(this);
		
		dbManager.printQuery("SELECT * FROM " + DBContract.TABLE_MATCH_PERF, null);
	}
	
	public void onResume() {
		
		super.onResume();
		
		MatchData[] matchData = dbManager.getMatchDataByColumns
				(databaseKeys, databaseValues);
		
		TableLayout table = (TableLayout)findViewById(R.id.dataTable);
		TableRow descriptorsRow = (TableRow)findViewById(R.id.descriptorsRow);
		
		descriptorsRow.removeAllViews();
		
		descriptorsRow.addView(new MyTextView(this, " ", 18));
		descriptorsRow.addView(new MyTextView(this, "Event", 18));
		descriptorsRow.addView(new MyTextView(this, "Team", 18));
		descriptorsRow.addView(new MyTextView(this, "Match #", 18));
		descriptorsRow.addView(new MyTextView(this, "User", 18));
		descriptorsRow.addView(new MyTextView(this, "Match Type", 18));
		descriptorsRow.addView(new MyTextView(this, "Comments", 18));
		
		if(matchData.length > 0) {
			for(MetricValue v : matchData[0].getMetricValues()) {
				
				descriptorsRow.addView
					(new MyTextView(this, v.getMetric().getMetricName(), 18));
			}
		}
		
		table.removeAllViews();
		table.addView(descriptorsRow);
		
		for(int i = 0; i < matchData.length; i++) {
			
			int color;
			
			if(i % 2 == 0)
				color = Color.BLUE;
			else
				color = Color.TRANSPARENT;
			
			
			Event[] eventArr = dbManager.getEventsByColumns
					(new String[] {DBContract.COL_EVENT_ID}, 
							new String[] {Integer.toString(matchData[i].getEventID())});
			
			Robot[] robotsArr = dbManager.getRobotsByColumns
					(new String[] {DBContract.COL_ROBOT_ID}, 
							new String[] {Integer.toString(matchData[i].getRobotID())});
			
			User[] userArr = dbManager.getUsersByColumns
					(new String[] {DBContract.COL_USER_ID}, 
							new String[] {Integer.toString(matchData[i].getUserID())});
			
			MyButton editButton = new MyButton(this, "Edit Data", this);
			editButton.setId(EDIT_BUTTON_ID);
			editButton.setTag(Integer.valueOf(matchData[i].getMatchID()));
			
			ArrayList<View> viewArr = new ArrayList<View>();
			
			viewArr.add(editButton);
			viewArr.add(new MyTextView(this, eventArr[0].getEventName(), 18));
			viewArr.add(new MyTextView(this, Integer.toString(robotsArr[0].getTeamNumber()), 
					18));
			viewArr.add(new MyTextView(this, Integer.toString(matchData[i].getMatchNumber()), 
					18));
			if(userArr.length > 0)
				viewArr.add(new MyTextView(this, userArr[0].getName(), 18));
			else 
				viewArr.add(new MyTextView(this, " ", 18));
			viewArr.add(new MyTextView(this, matchData[i].getMatchType(), 18));
			
			//Put a character limit on the comments string
			String displayedString = new String();
			
			if(matchData[i].getComments().length() < COMMENT_CHAR_LIMIT)
				displayedString = matchData[i].getComments();
			else
				displayedString = matchData[i].getComments().
					substring(0, COMMENT_CHAR_LIMIT - 1);
				
			viewArr.add(new MyTextView(this, displayedString, 18));
			
			//Add the metric data
			MetricValue[] metricValues = matchData[i].getMetricValues();
			
			for(int k = 0; k < metricValues.length; k++) {
				viewArr.add(new MyTextView(this, metricValues[k].
						getValueAsHumanReadableString(), 18));
			}
			
			//Add the data row
			table.addView(new MyTableRow(this, viewArr.toArray(new View[0]), color));
		}
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
}
