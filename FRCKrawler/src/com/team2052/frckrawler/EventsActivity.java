package com.team2052.frckrawler;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.example.frckrawler.R;
import com.team2052.frckrawler.database.DatabaseManager;
import com.team2052.frckrawler.database.structures.Event;
import com.team2052.frckrawler.gui.MyButton;
import com.team2052.frckrawler.gui.MyTableRow;
import com.team2052.frckrawler.gui.MyTextView;

public class EventsActivity extends TabActivity implements OnClickListener {
	
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
		TableRow descriptorsRow = (TableRow)findViewById(R.id.descriptorsRow);
		
		table.removeAllViews();
		table.addView(descriptorsRow);
		
		for(int i = 0; i < events.length; i++) {
			
			int color;
			
			if(i % 2 == 0)
				color = Color.BLUE;
			else
				color = Color.TRANSPARENT;
			
			table.addView(new MyTableRow(this, new View[] {
					new MyButton(this, "Edit Event", this, 
							Integer.valueOf(events[i].getEventID())),
					new MyTextView(this, events[i].getEventName()),
					new MyTextView(this, events[i].getLocation()),
					new MyTextView(this, events[i].getGameName()),
					new MyTextView(this, events[i].getDateStamp().toString()),
					new MyTextView(this, events[i].getFMSID())
			}, color));
		}
	}
	
	public void onClick(View v) {
		
		if(v.getId() == R.id.addEventButton) {
			
			Intent i = new Intent(this, AddEventDialogActivity.class);
			startActivity(i);
			
		} else {
			
			Intent i = new Intent(this, EditEventDialogActivity.class);
			i.putExtra(EditEventDialogActivity.EVENT_ID_EXTRA, 
					((Integer)v.getTag()).intValue());
			startActivity(i);
		}
	}
}
