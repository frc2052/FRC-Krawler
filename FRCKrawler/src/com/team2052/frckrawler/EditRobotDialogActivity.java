package com.team2052.frckrawler;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.example.frckrawler.R;
import com.team2052.frckrawler.database.DBManager;

public class EditRobotDialogActivity extends Activity implements OnClickListener {
	
	public static final String ROBOT_ID_EXTRA = "com.team2052.frckrawler.robotIDExtra";
	
	private DBManager dbManager;
	
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialogactivity_edit_robot);
		
		findViewById(R.id.save).setOnClickListener(this);
		findViewById(R.id.cancel).setOnClickListener(this);
		findViewById(R.id.remove).setOnClickListener(this);
		
		dbManager = DBManager.getInstance(this);
	}
	
	public void onResume() {
		
		super.onResume();
	}

	public void onClick(View v) {
		
		switch(v.getId()) {
			case R.id.save:
				
				finish();
				
				break;
				
			case R.id.remove:
				
				dbManager.removeRobot(Integer.parseInt(
						getIntent().getStringExtra(ROBOT_ID_EXTRA)));
				
				finish();
				
				break;
				
			case R.id.cancel:
				
				finish();
		}
		
	}
}
