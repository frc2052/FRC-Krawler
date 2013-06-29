package com.team2052.frckrawler;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.database.DBManager;

public class EditGameDialogActivity extends Activity implements OnClickListener, DialogInterface.OnClickListener{
	
	public static final String GAME_NAME_EXTRA = "com.team2052.frckrawler.editGameExtra";
	
	private DBManager dbManager;
	
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialogactivity_edit_game);
		
		((Button)findViewById(R.id.cancel)).setOnClickListener(this);
		((Button)findViewById(R.id.remove)).setOnClickListener(this);
		((Button)findViewById(R.id.saveList)).setOnClickListener(this);
		
		dbManager = DBManager.getInstance(this);
	}
	
	public void onResume() {
		
		super.onResume();
		
		((TextView)findViewById(R.id.nameVal)).setText(getIntent().getStringExtra(GAME_NAME_EXTRA));
	}
	
	
	/*****
	 * Method: onClick
	 * 
	 * @param v
	 * 
	 * Summary: This is the callback method for the Views in this activity.
	 *****/
	
	public void onClick(View v) {
		
		switch(v.getId()) {
			case R.id.saveList:
				
				finish();
			
				break;
				
			case R.id.cancel:
				
				finish();
				
				break;
		
			case R.id.remove:
				
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				
				builder.setMessage("Are you sure you want to remove this game from the database? " +
						"This will remove all robots, metrics, and match data associated with " +
						"this game from the database. It will be cast into the cold void of " +
						"cyberspace for eternity.");
				builder.setTitle("");
				builder.setPositiveButton("Yes", this);
				builder.setNegativeButton("No", this);
				
				builder.show();
				
				break;
		}
		
	}
	
	
	/*****
	 * Method: onClick
	 * 
	 * Summary: This is the callback method for the AlertDialog started by this
	 * activity.
	 *****/
	
	public void onClick(DialogInterface dialog, int which) {
		
		if(which == DialogInterface.BUTTON_POSITIVE) {
			
			dbManager.removeGame(getIntent().getStringExtra(GAME_NAME_EXTRA));
			dialog.dismiss();
			finish();
			
		} else {
			
			dialog.dismiss();
		}
	}
}
