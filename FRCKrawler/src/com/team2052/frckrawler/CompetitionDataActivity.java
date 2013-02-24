package com.team2052.frckrawler;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.example.frckrawler.R;
import com.team2052.frckrawler.database.DBContract;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.Game;

public class CompetitionDataActivity extends StackableTabActivity implements OnClickListener {
	
	private static final int EDIT_BUTTON_ID = 1;
	
	private DBManager dbManager;
	
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_competition_data);
		
		dbManager = DBManager.getInstance(this);
	}
	
	public void onResume() {
		
		super.onResume();
		
		TableLayout table = (TableLayout)findViewById(R.id.dataTable);
		TableRow descriptorsRow = (TableRow)findViewById(R.id.descriptorsRow);
		
		
		
		
		table.removeAllViews();
		table.addView(descriptorsRow);
	}
	
	public void onClick(View v) {
		
		switch(v.getId()) {
			case R.id.addData:
				
				break;
				
			case EDIT_BUTTON_ID:
				
				break;
		}
	}
}
