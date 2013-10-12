package com.team2052.frckrawler;

import java.util.Date;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TableLayout;
import android.widget.TextView;

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
	private static final int LISTS_ID = 7;
	private static final int IMPORT_ID = 8;
	
	private DBManager dbManager;
	
	@Override
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
	
	@Override
	public void onResume() {
		super.onResume();
		new GetEventsTask().execute();
	}
	
	public void postResults(Event[] events) {
		
		TableLayout table = (TableLayout)findViewById(R.id.eventsDataTable);
		table.removeAllViews();
		
		MyTableRow descriptorsRow = new MyTableRow(this);
		descriptorsRow.addView(new MyTextView(this, " ", 18));
		descriptorsRow.addView(new MyTextView(this, "Name", 18));
		descriptorsRow.addView(new MyTextView(this, "Location", 18));
		descriptorsRow.addView(new MyTextView(this, "Game", 18));
		descriptorsRow.addView(new MyTextView(this, "Date", 18));
		table.addView(descriptorsRow);
		
		for(int i = 0; i < events.length; i++) {
			int color;
			
			if(i % 2 == 0)
				color = GlobalValues.ROW_COLOR;
			else
				color = Color.TRANSPARENT;
			
			MyButton editEventButton = new MyButton(this, "Edit Event", this, 
					Integer.valueOf(events[i].getEventID()));
			editEventButton.setId(EDIT_EVENT_ID);
			
			MyButton attendingTeamsButton = new MyButton(this, "Attending Teams", this,
					Integer.valueOf(events[i].getEventID()));
			attendingTeamsButton.setId(ATTENDING_TEAMS_ID);
			
			MyButton robotsButton = new MyButton(this, "Robots", this, 
					Integer.valueOf(events[i].getEventID()));
			robotsButton.setId(ROBOTS_ID);
			
			MyButton matchDataButton = new MyButton(this, "Match Data", this, 
					Integer.valueOf(events[i].getEventID()));
			matchDataButton.setId(COMP_DATA_ID);
			
			MyButton compiledDataButton = new MyButton(this, "Summary", this, 
					Integer.valueOf(events[i].getEventID()));
			compiledDataButton.setId(COMPILED_DATA_ID);
			
			MyButton listsButton = new MyButton(this, "Lists", this, 
					Integer.valueOf(events[i].getEventID()));
			listsButton.setId(LISTS_ID);
			
			MyButton importButton = new MyButton(this, "Import", this, 
					Integer.valueOf(events[i].getEventID()));
			importButton.setId(IMPORT_ID);
			
			Date dateStamp = events[i].getDateStamp();
			String dateString = " " + dateStamp.getMonth() + "/" + dateStamp.getDay() + 
					"/" +(dateStamp.getYear() + 1900);
			
			table.addView(new MyTableRow(this, new View[] {
					editEventButton,
					new MyTextView(this, events[i].getEventName(), 18),
					new MyTextView(this, events[i].getLocation(), 18),
					new MyTextView(this, events[i].getGameName(), 18),
					new MyTextView(this, dateString, 18),
					attendingTeamsButton,
					robotsButton,
					matchDataButton,
					compiledDataButton,
					listsButton,
					importButton
			}, color));
		}
	}
	
	@Override
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
				i.putExtra(RawMatchDataActivity.LIMIT_LOADING_EXTRA, true);
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
				
			case LISTS_ID:
				i = new Intent(this, ListsActivity.class);
				i.putExtra(ListsActivity.EVENT_ID_EXTRA, 
						Integer.parseInt(v.getTag().toString()));
				startActivity(i);
				break;
				
			case IMPORT_ID:
				ConnectivityManager connMgr = (ConnectivityManager) 
						getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
				
				if (networkInfo != null && networkInfo.isConnected()) {
					i = new Intent(this, ImportDialogActivity.class);
					i.putExtra(ImportDialogActivity.EVENT_ID_EXTRA, 
							Integer.parseInt(v.getTag().toString()));
					startActivity(i);
				} else {
					AlertDialog.Builder b = new AlertDialog.Builder(this);
					b.setMessage("You must have internet connection to import event data " +
							"and OPR from the web.");
					b.setNeutralButton("Close", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
					b.show();
				}
				break;
		}
	}
	
	private class GetEventsTask extends AsyncTask<Void, Void, Event[]> {
		private int eventNum = 0;
		
		@Override
		protected Event[] doInBackground(Void... params) {
			Event[] e = dbManager.getEventsByColumns(databaseKeys, databaseValues);
			eventNum = e.length;
			return e;
		}
		
		@Override
		protected void onPostExecute(Event[] events) {
			((TextView)findViewById(R.id.eventNum)).setText(eventNum + " Events");
			postResults(events);
		}
	}
}
