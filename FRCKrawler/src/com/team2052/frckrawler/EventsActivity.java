package com.team2052.frckrawler;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TableLayout;

import com.example.frckrawler.R;
import com.team2052.frckrawler.database.DatabaseManager;
import com.team2052.frckrawler.database.structures.Event;
import com.team2052.frckrawler.gui.*;

public class EventsActivity extends TabActivity {
	
	private DatabaseManager dbManager;
	
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_events);
		
		dbManager = DatabaseManager.getInstance(this);
	}
	
	public void onStart() {
		
		super.onStart();
		
		Event[] events = dbManager.getAllEvents();
		TableLayout table = (TableLayout)findViewById(R.id.eventsDataTable);
		
		for(int i = 0; i < events.length; i++) {
			
			int color;
			
			if(i % 2 == 0)
				color = Color.BLUE;
			else
				color = Color.TRANSPARENT;
			
			table.addView(new MyTableRow(this, new View[] {
					new MyTextView(this, events[i].getEventName()),
					new MyTextView(this, events[i].getLocation()),
					new MyTextView(this, events[i].getGameName()),
					new MyTextView(this, events[i].getDateStamp().toString()),
					new MyTextView(this, events[i].getFMSID())
			}, color));
		}
	}
}
