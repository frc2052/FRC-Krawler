package com.team2052.frckrawler;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.example.frckrawler.R;
import com.team2052.frckrawler.gui.SidewaysTextView;

public class RobotsActivity extends StackableTabActivity implements OnClickListener {
	
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_robots);
	}
	
	public void onStart() {
		
		super.onStart();
	}

	public void onClick(View v) {
		
		switch(v.getId()) {
		
		}
	}

}
