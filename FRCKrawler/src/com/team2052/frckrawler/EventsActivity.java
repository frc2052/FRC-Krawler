package com.team2052.frckrawler;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.team2052.frckrawler.database.DBContract;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.Event;
import com.team2052.frckrawler.database.structures.Robot;
import com.team2052.frckrawler.gui.MyButton;
import com.team2052.frckrawler.gui.MyTableRow;
import com.team2052.frckrawler.gui.MyTextView;

public class EventsActivity extends StackableTabActivity implements OnClickListener {
	
	private static final int EDIT_EVENT_ID = 1;
	private static final int ATTENDING_TEAMS_ID = 2;
	private static final int ROBOTS_ID = 3;
	private static final int COMP_DATA_ID = 4;
	private static final int DRIVER_DATA_ID = 5;
	private static final int COMPILED_DATA_ID = 6;
	
	private DBManager dbManager;
	
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_events);
		
		try {
			String value = 
					databaseValues[getAddressOfDatabaseKey(DBContract.COL_GAME_NAME)];
			findViewById(R.id.addEventButton).setOnClickListener(this);
			
		} catch(ArrayIndexOutOfBoundsException e) {
			findViewById(R.id.addEventButton).setEnabled(false);
		}
		
		dbManager = DBManager.getInstance(this);
	}
	
	public void onResume() {
		
		super.onResume();
		new GetEventsTask().execute();
	}
	
	public void postResults(Event[] events) {
		
		TableLayout table = (TableLayout)findViewById(R.id.eventsDataTable);
		TableRow descriptorsRow = (TableRow)findViewById(R.id.descriptorsRow);
		
		table.removeAllViews();
		table.addView(descriptorsRow);
		
		for(int i = 0; i < events.length; i++) {
			
			int color;
			
			if(i % 2 == 0)
				color = GlobalSettings.ROW_COLOR;
			else
				color = Color.TRANSPARENT;
			
			MyButton editEvent = new MyButton(this, "Edit Event", this, 
					Integer.valueOf(events[i].getEventID()));
			editEvent.setId(EDIT_EVENT_ID);
			
			MyButton attendingTeams = new MyButton(this, "Attending Teams", this,
					Integer.valueOf(events[i].getEventID()));
			attendingTeams.setId(ATTENDING_TEAMS_ID);
			
			MyButton robots = new MyButton(this, "Robots", this, 
					Integer.valueOf(events[i].getEventID()));
			robots.setId(ROBOTS_ID);
			
			MyButton data = new MyButton(this, "Match Data", this, 
					Integer.valueOf(events[i].getEventID()));
			data.setId(COMP_DATA_ID);
			
			MyButton compiledData = new MyButton(this, "Compiled Data", this, 
					Integer.valueOf(events[i].getEventID()));
			compiledData.setId(COMPILED_DATA_ID);
			
			table.addView(new MyTableRow(this, new View[] {
					editEvent,
					new MyTextView(this, events[i].getEventName(), 18),
					new MyTextView(this, events[i].getLocation(), 18),
					new MyTextView(this, events[i].getGameName(), 18),
					new MyTextView(this, events[i].getDateStamp().toString(), 18),
					new MyTextView(this, events[i].getFMSID(), 18),
					attendingTeams,
					robots,
					data,
					compiledData
			}, color));
		}
	}
	
	public void onClick(View v) {
		
		Intent i;
		
		switch(v.getId()) {
			case R.id.addEventButton:
				
				if(parents[getAddressOfDatabaseKey(DBContract.COL_GAME_NAME)] != null) {
					
					i = new Intent(this, AddEventDialogActivity.class);
					i.putExtra(AddEventDialogActivity.GAME_NAME_EXTRA,
							parents[getAddressOfDatabaseKey(DBContract.COL_GAME_NAME)]);
					startActivity(i);
				}
				
				break;
				
			case EDIT_EVENT_ID:
			
				i = new Intent(this, EditEventDialogActivity.class);
				i.putExtra(EditEventDialogActivity.EVENT_ID_EXTRA, v.getTag().toString());
				startActivity(i);
				
				break;
				
			case ATTENDING_TEAMS_ID:
				
				i = new Intent(this, AttendingTeamsDialogActivity.class);
				i.putExtra(AttendingTeamsDialogActivity.GAME_NAME_EXTRA, 
						databaseValues[getAddressOfDatabaseKey(DBContract.COL_GAME_NAME)]);
				i.putExtra(AttendingTeamsDialogActivity.EVENT_ID_EXTRA, v.getTag().toString());
				startActivity(i);
				
				break;
				
			case ROBOTS_ID:
				
				Robot[] attendingRobots = dbManager.getRobotsAtEvent
								(Integer.parseInt(v.getTag().toString()));
				
				String[] dbValsArr = new String[attendingRobots.length];
				String[] dbKeysArr = new String[attendingRobots.length];
				
				for(int k = 0; k < attendingRobots.length; k ++) {
					
					dbValsArr[k] = Integer.toString(attendingRobots[k].getID());
					dbKeysArr[k] = DBContract.COL_ROBOT_ID;
				}
				
				i = new Intent(this, RobotsActivity.class);
				i.putExtra(DB_VALUES_EXTRA, dbValsArr);
				i.putExtra(DB_KEYS_EXTRA, dbKeysArr);
				startActivity(i);
				
				break;
				
			case COMP_DATA_ID:
				
				i = new Intent(this, RawMatchDataActivity.class);
				i.putExtra(PARENTS_EXTRA, new String[] {});
				i.putExtra(DB_VALUES_EXTRA, new String[] {v.getTag().toString()});
				i.putExtra(DB_KEYS_EXTRA, new String[] {DBContract.COL_EVENT_ID});
				startActivity(i);
				
				break;
				
			case DRIVER_DATA_ID:
				
				i = new Intent(this, DriverDataActivity.class);
				i.putExtra(PARENTS_EXTRA, new String[] {});
				i.putExtra(DB_VALUES_EXTRA, new String[] {v.getTag().toString()});
				i.putExtra(DB_KEYS_EXTRA, new String[] {DBContract.COL_EVENT_ID});
				startActivity(i);
				
				break;
				
			case COMPILED_DATA_ID:
				
				i = new Intent(this, QueryActivity.class);
				i.putExtra(PARENTS_EXTRA, new String[] {});
				i.putExtra(DB_VALUES_EXTRA, new String[] {v.getTag().toString()});
				i.putExtra(DB_KEYS_EXTRA, new String[] {DBContract.COL_EVENT_ID});
				startActivity(i);
				
				break;
		}
	}
	
	private class GetEventsTask extends AsyncTask<Void, Void, Event[]> {
		
		protected Event[] doInBackground(Void... params) {
			return dbManager.getEventsByColumns(databaseKeys, databaseValues);
		}
		
		protected void onPostExecute(Event[] events) {
			postResults(events);
		}
	}
}
