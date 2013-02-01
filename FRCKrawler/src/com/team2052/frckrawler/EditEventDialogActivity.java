package com.team2052.frckrawler;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

import com.example.frckrawler.R;
import com.team2052.frckrawler.database.*;
import com.team2052.frckrawler.database.structures.Event;

public class EditEventDialogActivity extends Activity implements OnClickListener, DialogInterface.OnClickListener {
	
	public static final String EVENT_ID_EXTRA = "com.team2052.frckrawler.editEventID";
	
	private DatabaseManager db;
	
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialogactivity_edit_event);
		
		((Button)findViewById(R.id.save)).setOnClickListener(this);
		((Button)findViewById(R.id.remove)).setOnClickListener(this);
		((Button)findViewById(R.id.cancel)).setOnClickListener(this);
		
		db = DatabaseManager.getInstance(this);
	}
	
	public void onStart() {
		
		super.onStart();
		
		Event[] arr = db.getEventsByColumns(new String[] {DatabaseContract.COL_EVENT_ID}, 
				new String[] {getIntent().getStringExtra(EVENT_ID_EXTRA)});
		Event e;
		
		if(arr.length > 0)
			e = arr[0];
		else
			return;
		
		GregorianCalendar date = new GregorianCalendar();
		date.setTime(e.getDateStamp());
		
		((EditText)findViewById(R.id.eventName)).setText(e.getEventName());
		((DatePicker)findViewById(R.id.date)).updateDate(date.get(Calendar.YEAR), 
				date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH));
		((EditText)findViewById(R.id.location)).setText(e.getLocation());
		((EditText)findViewById(R.id.fmsID)).setText(e.getFMSID());
	}
	
	public void onClick(View v) {
		
		switch(v.getId()) {
			case R.id.save :
				
				DatePicker datePicker = (DatePicker)findViewById(R.id.date);
				GregorianCalendar date = new GregorianCalendar(datePicker.getYear(), 
						datePicker.getMonth(), datePicker.getDayOfMonth());
				String time = Long.toString(date.getTime().getTime());
				
				db.updateEvents(new String[] {DatabaseContract.COL_EVENT_ID}, 
						new String[] {getIntent().getStringExtra(EVENT_ID_EXTRA)}, 
						new String[] {
							DatabaseContract.COL_EVENT_NAME,
							DatabaseContract.COL_DATE_STAMP,
							DatabaseContract.COL_LOCATION,
							DatabaseContract.COL_FMS_EVENT_ID
							}, 
						new String[] {
							((EditText)findViewById(R.id.eventName)).getText().toString(),
							time,
							((EditText)findViewById(R.id.location)).getText().toString(),
							((EditText)findViewById(R.id.fmsID)).getText().toString()
							}
				);
				
				finish();
				
				break;
				
			case R.id.remove :
				
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				
				builder.setMessage("Are you sure you want to remove this event from the database? " +
						"This will remove all of its match data and " +
						"it will be cast into the cold void of cyberspace for eternity.");
				builder.setTitle("");
				builder.setPositiveButton("Yes", this);
				builder.setNegativeButton("No", this);
				
				builder.show();
				
				break;
				
			case R.id.cancel :
				
				finish();
				
				break;
		}
	}

	public void onClick(DialogInterface dialog, int which) {
		
		if(which == DialogInterface.BUTTON_POSITIVE) {
			
			db.removeEvent(Integer.parseInt(getIntent().
					getStringExtra(EVENT_ID_EXTRA)));
			
			dialog.dismiss();
			finish();
			
		} else if(which == DialogInterface.BUTTON_NEGATIVE) {
			
			dialog.dismiss();
		}
	}
}
