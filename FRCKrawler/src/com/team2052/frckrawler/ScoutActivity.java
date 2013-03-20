package com.team2052.frckrawler;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class ScoutActivity extends Activity {
	
	public static final int SCOUT_TYPE_MATCH = 1;
	public static final int SCOUT_TYPE_PIT = 2;
	public static final int SCOUT_TYPE_DRIVER = 3;
	
	public static final String SCOUT_TYPE_EXTRA = 
			"com.team2052.frckrawler.scoutTypeExtra";
	
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scouting);
		
		TextView title = (TextView)findViewById(R.id.scoutingType);
		
		switch(getIntent().getIntExtra(SCOUT_TYPE_EXTRA, 1)) {
			case SCOUT_TYPE_MATCH:
				title.setText("Match Scouting");
				break;
				
			case SCOUT_TYPE_PIT:
				title.setText("Pit Scouting");
				break;
				
			case SCOUT_TYPE_DRIVER:
				title.setText("Driver Scouting");
				break;
		}
	}
}
