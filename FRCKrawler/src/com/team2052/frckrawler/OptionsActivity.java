package com.team2052.frckrawler;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class OptionsActivity extends TabActivity implements OnClickListener {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_options);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.save:
				
				break;
				
			case R.id.cancel:
				
				finish();
				break;
				
			case R.id.weightHelp:
				
				break;
				
			case R.id.generateRobots:
				
				break;
				
			case R.id.generateHelp:
				
				break;
		}
	}
}
