package com.team2052.frckrawler;

import com.team2052.frckrawler.R;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TableLayout;
import android.widget.TableRow;

public class DriverDataActivity extends StackableTabActivity implements OnClickListener {
	
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_driver_data);
		
		findViewById(R.id.addData).setOnClickListener(this);
	}
	
	public void onResume() {
		
		super.onResume();
		
		TableLayout table = (TableLayout)findViewById(R.id.robotsDataTable);
		TableRow descriptorsRow = new TableRow(this);
		
		table.removeAllViews();
		table.addView(descriptorsRow);
	}
	
	public void onClick(View v) {
		
		
	}
}
