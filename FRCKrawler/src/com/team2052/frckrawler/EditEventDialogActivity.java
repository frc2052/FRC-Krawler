package com.team2052.frckrawler;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.team2052.frckrawler.database.DBContract;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.Event;

public class EditEventDialogActivity extends Activity 
						implements OnClickListener, DialogInterface.OnClickListener {
	public static final String EVENT_ID_EXTRA = "com.team2052.frckrawler.editEventID";
	private DBManager db;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialogactivity_edit_event);
		((Button)findViewById(R.id.save)).setOnClickListener(this);
		((Button)findViewById(R.id.remove)).setOnClickListener(this);
		((Button)findViewById(R.id.cancel)).setOnClickListener(this);
		db = DBManager.getInstance(this);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		Event[] arr = db.getEventsByColumns(new String[] {DBContract.COL_EVENT_ID}, 
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
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.save :
				DatePicker datePicker = (DatePicker)findViewById(R.id.date);
				GregorianCalendar date = new GregorianCalendar(datePicker.getYear(), 
						datePicker.getMonth(), datePicker.getDayOfMonth());
				String time = Long.toString(date.getTime().getTime());
				db.updateEvents(new String[] {DBContract.COL_EVENT_ID}, 
						new String[] {getIntent().getStringExtra(EVENT_ID_EXTRA)}, 
						new String[] {
							DBContract.COL_EVENT_NAME,
							DBContract.COL_DATE_STAMP,
							DBContract.COL_LOCATION,
							DBContract.COL_FMS_EVENT_ID
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
				builder.setMessage("Are you sure you want to remove this event?");
				builder.setPositiveButton("Yes", this);
				builder.setNegativeButton("No", this);
				builder.show();
				break;
				
			case R.id.cancel :
				finish();
				break;
		}
	}

	@Override
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
