package com.team2052.frckrawler;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

import com.example.frckrawler.R;
import com.team2052.frckrawler.database.DatabaseManager;

public class AddTeamDialogActivity extends Activity implements OnClickListener {
	
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialogactivity_add_team);
		
		((Button)findViewById(R.id.addTeamAccept)).setOnClickListener(this);
		((Button)findViewById(R.id.cancel)).setOnClickListener(this);
	}
	
	public void onClick(View v) {
		
		if(v.getId() == R.id.cancel) {
			
			finish();
			
		} else if(v.getId() == R.id.addTeamAccept) {
			
			int rookieYear = -1;
			
			try {
				
				rookieYear = Integer.parseInt(((TextView)findViewById(R.id.rookieYearVal)).getText().toString());
			
			} catch (NumberFormatException e) {}
			
			try {
				
				DatabaseManager.getInstance(this).addTeam(
						Integer.parseInt(((TextView)findViewById(R.id.numberVal)).getText().toString()), 
						((TextView)findViewById(R.id.nameVal)).getText().toString(), 
						((TextView)findViewById(R.id.schoolVal)).getText().toString(), 
						((TextView)findViewById(R.id.cityVal)).getText().toString(), 
						rookieYear, 
						((TextView)findViewById(R.id.websiteVal)).getText().toString(), 
						((Spinner)findViewById(R.id.stateVal)).getSelectedItem().toString(), 
						((TextView)findViewById(R.id.colorsVal)).getText().toString());
				
				finish();
				
			} catch (NumberFormatException e) {
				Toast.makeText(this, "Please enter a team number.", Toast.LENGTH_SHORT).show();
			}
		}
	}
}
