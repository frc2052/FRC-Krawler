package com.team2052.frckrawler;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

import com.example.frckrawler.R;
import com.team2052.frckrawler.database.*;
import com.team2052.frckrawler.database.structures.Event;
import com.team2052.frckrawler.gui.*;

public class EventsActivity extends StackableTabActivity implements OnClickListener {
	
	private static final int EDIT_EVENT_ID = 1;
	private static final int ATTENDING_TEAMS_ID = 2;
	private static final int ROBOTS_ID = 3;
	
	private DBManager dbManager;
	
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_events);
		
		findViewById(R.id.addEventButton).setOnClickListener(this);
		
		dbManager = DBManager.getInstance(this);
	}
	
	public void onResume() {
		
		super.onResume();
		
		Event[] events = dbManager.getEventsByColumns(databaseKeys, databaseValues);
		TableLayout table = (TableLayout)findViewById(R.id.eventsDataTable);
		TableRow descriptorsRow = (TableRow)findViewById(R.id.descriptorsRow);
		
		table.removeAllViews();
		table.addView(descriptorsRow);
		
		for(int i = 0; i < events.length; i++) {
			
			int color;
			
			if(i % 2 == 0)
				color = Color.BLUE;
			else
				color = Color.TRANSPARENT;
			
			MyButton editEvent = new MyButton(this, "Edit Event", this, 
					Integer.valueOf(events[i].getEventID()));
			editEvent.setId(EDIT_EVENT_ID);
			
			MyButton attendingTeams = new MyButton(this, "Attending Teams", this,
					Integer.valueOf(events[i].getEventID()));
			attendingTeams.setId(ATTENDING_TEAMS_ID);
			
			MyButton robots = new MyButton(this, "Data", this, 
					Integer.valueOf(events[i].getEventID()));
			robots.setId(ROBOTS_ID);
			
			table.addView(new MyTableRow(this, new View[] {
					editEvent,
					new MyTextView(this, events[i].getEventName(), 18),
					new MyTextView(this, events[i].getLocation(), 18),
					new MyTextView(this, events[i].getGameName(), 18),
					new MyTextView(this, events[i].getDateStamp().toString(), 18),
					new MyTextView(this, events[i].getFMSID(), 18),
					attendingTeams,
					robots
			}, color));
		}
	}
	
	public void onClick(View v) {
		
		Intent i;
		
		switch(v.getId()) {
			case R.id.addEventButton:
			
				i = new Intent(this, AddEventDialogActivity.class);
				i.putExtra(AddEventDialogActivity.GAME_NAME_EXTRA,
						parents[getAddressOfDatabaseKey(DBContract.COL_GAME_NAME)]);
				startActivity(i);
				
				break;
				
			case EDIT_EVENT_ID:
			
				i = new Intent(this, EditEventDialogActivity.class);
				i.putExtra(EditEventDialogActivity.EVENT_ID_EXTRA, v.getTag().toString());
				startActivity(i);
				
			case ATTENDING_TEAMS_ID:
				
				i = new Intent(this, AttendingTeamsDialogActivity.class);
				i.putExtra(AttendingTeamsDialogActivity.GAME_NAME_EXTRA, 
						databaseValues[getAddressOfDatabaseKey(DBContract.COL_GAME_NAME)]);
				i.putExtra(AttendingTeamsDialogActivity.EVENT_ID_EXTRA, v.getTag().toString());
				startActivity(i);
				
				break;
				
			case ROBOTS_ID:
				
				
				
				break;
		}
	}
}
