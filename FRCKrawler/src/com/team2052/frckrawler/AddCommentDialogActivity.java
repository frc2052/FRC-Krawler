package com.team2052.frckrawler;

import com.example.frckrawler.R;

import android.app.Activity;
import android.os.Bundle;

public class AddCommentDialogActivity extends Activity {
	
	public static final String TEAM_NUMBER_EXTRA = "com.team2052.frckrawler.teamNumberExtra";
	
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialogactivity_add_comment);
		
	}

}
