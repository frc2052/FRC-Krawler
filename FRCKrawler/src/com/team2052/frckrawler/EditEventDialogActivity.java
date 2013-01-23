package com.team2052.frckrawler;

import com.example.frckrawler.R;

import android.app.Activity;
import android.os.Bundle;

public class EditEventDialogActivity extends Activity {
	
	public static final String EVENT_ID_EXTRA = "com.team2052.frckrawler.editEventID";
	
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialogactivity_edit_event);
	}

}
