package com.team2052.frckrawler;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.MetricValue;
import com.team2052.frckrawler.database.structures.Team;

public class AddTeamDialogActivity extends Activity implements OnClickListener {
	
	private DBManager dbManager;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode
			(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setContentView(R.layout.dialogactivity_add_team);
		
		((Button)findViewById(R.id.addTeamAccept)).setOnClickListener(this);
		((Button)findViewById(R.id.cancel)).setOnClickListener(this);
		((Spinner)findViewById(R.id.stateVal)).setSelection(24);
		
		dbManager = DBManager.getInstance(this);
	}
	
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.cancel) {
			finish();
			
		} else if(v.getId() == R.id.addTeamAccept) {
			int rookieYear = -1;
			
			try {
				rookieYear = Integer.parseInt(((TextView)findViewById
						(R.id.rookieYearVal)).getText().toString());
			} catch (NumberFormatException e) {}
			
			try {
				Team team = new Team(Integer.parseInt
						(((TextView)findViewById(R.id.numberVal)).getText().toString()), 
						((TextView)findViewById(R.id.nameVal)).getText().toString(), 
						((TextView)findViewById(R.id.schoolVal)).getText().toString(), 
						((TextView)findViewById(R.id.cityVal)).getText().toString(), 
						rookieYear, 
						((TextView)findViewById(R.id.websiteVal)).getText().toString(), 
						((Spinner)findViewById(R.id.stateVal)).getSelectedItem().toString(), 
						((TextView)findViewById(R.id.colorsVal)).getText().toString());
				new AddTeamTask().execute(team);
				
			} catch (NumberFormatException e) {
				Toast.makeText(this, "Please enter a team number.", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	private class AddTeamTask extends AsyncTask<Team, Void, Boolean> {
		
		@Override
		protected Boolean doInBackground(Team... team) {
			boolean added = true;
			
			try {
				added = dbManager.addTeams(team);
			} catch(NumberFormatException e) {}
			
			return added;
		}

		@Override
		protected void onPostExecute(Boolean added) {
			
			if(added) {
				setResult(RESULT_OK);
				finish();
			} else {
				Toast.makeText(getApplicationContext(), "Could not add team to database. " +
						"Number is already taken.", Toast.LENGTH_LONG).show();
			}
		}
	}
}
