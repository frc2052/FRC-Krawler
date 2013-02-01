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
	
	private DatabaseManager dbManager;
	
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_events);
		
		findViewById(R.id.addEventButton).setOnClickListener(this);
		
		dbManager = DatabaseManager.getInstance(this);
	}
	
	public void onResume() {
		
		super.onResume();
		
		Event[] events = dbManager.getEventsByColumns(databaseKeys, parents);
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
			
			table.addView(new MyTableRow(this, new View[] {
					editEvent,
					new MyTextView(this, events[i].getEventName(), 18),
					new MyTextView(this, events[i].getLocation(), 18),
					new MyTextView(this, events[i].getGameName(), 18),
					new MyTextView(this, events[i].getDateStamp().toString(), 18),
					new MyTextView(this, events[i].getFMSID(), 18)
			}, color));
		}
	}
	
	public void onClick(View v) {
		
		Intent i;
		
		switch(v.getId()) {
			case R.id.addEventButton:
			
				i = new Intent(this, AddEventDialogActivity.class);
				i.putExtra(AddEventDialogActivity.GAME_NAME_EXTRA,
						parents[getAddressOfDatabaseKey(DatabaseContract.COL_GAME_NAME)]);
				startActivity(i);
				
				break;
				
			case EDIT_EVENT_ID:
			
				i = new Intent(this, EditEventDialogActivity.class);
				i.putExtra(EditEventDialogActivity.EVENT_ID_EXTRA, 
						(v.getTag().toString()));
				startActivity(i);
		}
	}
}
