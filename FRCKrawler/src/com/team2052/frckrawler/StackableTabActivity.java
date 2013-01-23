package com.team2052.frckrawler;

import com.example.frckrawler.R;
import com.team2052.frckrawler.gui.SidewaysTextView;

import android.os.Bundle;
import android.widget.LinearLayout;

public class StackableTabActivity extends TabActivity {
	
	public static final String PARENTS_EXTRA = "com.team2052.frckrawler.parentsArrayExtra";
	public static final String PARENT_KEYS_EXTRA = "com.team2052.frckrawler.parentsKeysExtra";
	
	protected String[] parents;
	protected String[] databaseKeys;
	
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		parents = getIntent().getStringArrayExtra(PARENTS_EXTRA);
		databaseKeys = getIntent().getStringArrayExtra(PARENT_KEYS_EXTRA);
	}
	
	public void onStart() {
		
		super.onStart();
		
		LinearLayout l = (LinearLayout)findViewById(R.id.treeView);
		
		for(String s : parents)
			l.addView(new SidewaysTextView(this, s));
	}
}
