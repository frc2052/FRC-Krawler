package com.team2052.frckrawler;

import com.team2052.frckrawler.database.DBManager;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class QuerySortingDialogActivity extends Activity implements OnClickListener {
	
	public static final String EVENT_ID_EXTRA = "com.team2052.frckrawler.eventID";
	
	private DBManager dbManager;
	
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialogactivity_query_and_sorting);
		
		findViewById(R.id.save).setOnClickListener(this);
		findViewById(R.id.cancel).setOnClickListener(this);
		
		dbManager = DBManager.getInstance(this);
	}

	public void onClick(View v) {
		
		if(v.getId() == R.id.cancel) {
			
			finish();
			
		} else {
			
			finish();
		}
	}
}
