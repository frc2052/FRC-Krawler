package com.team2052.frckrawler;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

import com.example.frckrawler.R;
import com.team2052.frckrawler.database.DatabaseContract;
import com.team2052.frckrawler.database.DatabaseManager;
import com.team2052.frckrawler.database.structures.User;

public class EditUserDialogActivity extends Activity implements OnClickListener{
	
	public static final String USER_ID_EXTRA = "com.team2052.frckrawler.userID";
	
	private DatabaseManager dbManager;
	
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialogactivity_edit_user);
		
		((Button)findViewById(R.id.saveUser)).setOnClickListener(this);
		((Button)findViewById(R.id.cancel)).setOnClickListener(this);
		
		dbManager = DatabaseManager.getInstance(this);
	}
	
	public void onResume() {
		
		super.onResume();
		
		User[] arr = dbManager.getUsersByColumns(
				new String[] {DatabaseContract.COL_USER_ID}, 
				new String[] {getIntent().getStringExtra(USER_ID_EXTRA)});
		User u = arr[0];
		
		((TextView)findViewById(R.id.nameVal)).setText(u.getName());
		((CheckBox)findViewById(R.id.superuserVal)).setChecked(u.isSuperuser());
	} 
	
	public void onClick(View v) {
		
		switch(v.getId()) {
			case R.id.cancel :
			
				finish();
			
				break;
			
			case R.id.saveUser :
				
				String[] queryCols = new String[] {
						DatabaseContract.COL_USER_ID
				};
				
				String[] queryVals = new String[] {
						getIntent().getStringExtra(USER_ID_EXTRA)
				};
				
				String[] updateCols = new String[] {
						DatabaseContract.COL_USER_NAME,
						DatabaseContract.COL_SUPERUSER
				};
				
				String[] updateVals = new String[] {
						((TextView)findViewById(R.id.nameVal)).getText().toString(),
						Boolean.toString(
								((CheckBox)findViewById(R.id.superuserVal))
								.isChecked())
				};
				
				dbManager.updateUsers(queryCols, queryVals, updateCols, updateVals);
				
				finish();
			
				break;
		}
		
	}
}
