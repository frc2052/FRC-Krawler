package com.team2052.frckrawler;

import java.util.GregorianCalendar;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.frckrawler.R;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.Event;
import com.team2052.frckrawler.database.structures.Game;

public class AddEventDialogActivity extends Activity implements OnClickListener {
	
	public static final String GAME_NAME_EXTRA = "com.team2052.frckrawler.gameNameExtra";
	
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialogactivity_add_event);
		
		((Button)findViewById(R.id.addEvent)).setOnClickListener(this);
		((Button)findViewById(R.id.cancel)).setOnClickListener(this);
	}
	
	public void onClick(View v) {
		
		if(v.getId() == R.id.addEvent) {
			
			DatePicker date = (DatePicker)findViewById(R.id.date);
			
			int day = date.getDayOfMonth();
			int month = date.getMonth();
			int year = date.getYear();
			
			DBManager.getInstance(this).addEvent(new Event(
					((EditText)findViewById(R.id.eventName)).getText().toString(),
					getIntent().getStringExtra(GAME_NAME_EXTRA),
					new GregorianCalendar(year, month, day, 0, 0).getTime(),
					((EditText)findViewById(R.id.location)).getText().toString(),
					((EditText)findViewById(R.id.fmsID)).getText().toString()
					));
			
			finish();
			
		} else if(v.getId() == R.id.cancel) {
			
			finish();
		}
	}
}
