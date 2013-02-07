package com.team2052.frckrawler;

import com.example.frckrawler.R;
import com.team2052.frckrawler.gui.SidewaysTextView;

import android.os.Bundle;
import android.widget.LinearLayout;

public class StackableTabActivity extends TabActivity {
	
	public static final String PARENTS_EXTRA = "com.team2052.frckrawler.parentsArrayExtra";
	public static final String DB_VALUES_EXTRA = "com.team2052.frckrawler.dbValsExtra";
	public static final String DB_KEYS_EXTRA = "com.team2052.frckrawler.dbKeysExtra";
	
	protected String[] parents;
	protected String[] databaseValues;
	protected String[] databaseKeys;
	
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		parents = getIntent().getStringArrayExtra(PARENTS_EXTRA);
		databaseValues = getIntent().getStringArrayExtra(DB_VALUES_EXTRA);
		databaseKeys = getIntent().getStringArrayExtra(DB_KEYS_EXTRA);
		
		setNoRootActivitySelected();
	}
	
	public void onStart() {
		
		super.onStart();
		
		LinearLayout l = (LinearLayout)findViewById(R.id.treeView);
		
		for(String s : parents)
			l.addView(new SidewaysTextView(this, s));
	}
	
	
	/****
	 * Method: getAddressoOfDatabaseKey
	 * 
	 * @param key
	 * @return
	 * 
	 * Summary: returns the first address of the string passed as
	 * a parameter or -1 if it wasn't in the database.
	 *****/
	
	protected int getAddressOfDatabaseKey(String key) {
		
		for(int i = 0; i < databaseKeys.length; i++) {
			
			if(databaseKeys[i].equals(key))
				return i;
		}
		
		return -1;
	}
}
