package com.team2052.frckrawler;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

import com.example.frckrawler.R;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.*;

public class AddRobotDialogActivity extends Activity implements OnClickListener {
	
	public static final String TEAM_NUMBER_EXTRA = "com.team2052.frckrawler.teamNumberExtra";
	
	private DBManager dbManager;
	
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialogactivity_add_robot);
		
		findViewById(R.id.cancel).setOnClickListener(this);
		findViewById(R.id.addRobot).setOnClickListener(this);
		
		dbManager = DBManager.getInstance(this);
	}
	
	public void onResume() {
		
		super.onResume();
		
		//Set the choices for the spinner
		Game[] games = dbManager.getAllGames();
		String[] spinnerVals = new String[games.length];
		
		for(int i = 0; i < spinnerVals.length; i++) {
			spinnerVals[i] = games[i].getName();
		}
		
		Spinner gameSpinner = (Spinner)findViewById(R.id.gameSpinner);
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
				android.R.layout.simple_spinner_item,spinnerVals);
		
		gameSpinner.setAdapter(adapter);
	}

	public void onClick(View v) {
		
		switch(v.getId()) {
			case R.id.addRobot:
				
				dbManager.addRobot(new Robot(
						Integer.parseInt(getIntent().getStringExtra(TEAM_NUMBER_EXTRA)),
						((Spinner)findViewById(R.id.gameSpinner)).getSelectedItem().toString(),
						((EditText)findViewById(R.id.comments)).getText().toString()
						));
				
				finish();
				
				break;
				
			case R.id.cancel:
				
				finish();
				
				break;
		}
		
	}
}
