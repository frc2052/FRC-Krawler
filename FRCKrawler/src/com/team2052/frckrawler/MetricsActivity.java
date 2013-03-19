package com.team2052.frckrawler;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.database.DBContract;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.Metric;
import com.team2052.frckrawler.gui.*;

public class MetricsActivity extends StackableTabActivity implements OnClickListener {
	
	public static final String METRIC_CATEGORY_EXTRA = 
			"com.team2052.frckrawler.metricCategoryExtra";
	
	public static final int MATCH_PERF_METRICS = 1;
	public static final int ROBOT_METRICS = 2;
	public static final int DRIVER_METRICS = 3;
	
	private static final int EDIT_BUTTON_ID = 1;
	private static final int DESCRIPTION_CHAR_LIMIT = 20;
	
	private int metricCategory;
	private DBManager dbManager;
	
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_metrics);
		
		findViewById(R.id.addMetric).setOnClickListener(this);
		
		TextView title = (TextView)findViewById(R.id.title);
		
		metricCategory = getIntent().getIntExtra(METRIC_CATEGORY_EXTRA, -1);
		
		if(metricCategory == MATCH_PERF_METRICS)
			title.setText("Match Performance Metrics");
		else if(metricCategory == ROBOT_METRICS)
			title.setText("Robot Metrics");
		else if(metricCategory == DRIVER_METRICS)
			title.setText("Driver Metrics");
		else {
			metricCategory = MATCH_PERF_METRICS;
			title.setText("Match Performance Metrics");
		}
		
		dbManager = DBManager.getInstance(this);
	}
	
	public void onResume() {
		
		super.onResume();
		
		TableLayout table = (TableLayout)findViewById(R.id.metricsDataTable);
		TableRow descriptorsRow = (TableRow)findViewById(R.id.descriptorsRow);
		
		table.removeAllViews();
		table.addView(descriptorsRow);
		
		Metric[] metrics;
		
		switch(metricCategory) {
			case MATCH_PERF_METRICS:
				
				metrics = dbManager.getMatchPerformanceMetricsByColumns
					(new String[] {DBContract.COL_GAME_NAME}, 
						new String[] {databaseValues[this.getAddressOfDatabaseKey
						                             (DBContract.COL_GAME_NAME)]});
				
				break;
				
			case ROBOT_METRICS:
				
				metrics = dbManager.getRobotMetricsByColumns
						(new String[] {DBContract.COL_GAME_NAME}, 
								new String[] {databaseValues[this.getAddressOfDatabaseKey
								                             (DBContract.COL_GAME_NAME)]});
				
				break;
				
			case DRIVER_METRICS:
				
				
				
				//break;
				
			default:
				
				metrics = new Metric[0];
		}
		
		for(int i = 0; i < metrics.length; i++) {
			
			int color;
			
			if(i % 2 == 0)
				color = GlobalSettings.ROW_COLOR;
			else
				color = Color.TRANSPARENT;
			
			MyButton editButton = new MyButton(this, "Edit Metric", this, 
					Integer.valueOf(metrics[i].getID()));
			editButton.setId(EDIT_BUTTON_ID);
			
			String descriptionString;
			
			if(metrics[i].getDescription().length() >= DESCRIPTION_CHAR_LIMIT) {
				descriptionString = metrics[i].getDescription().
						substring(0, DESCRIPTION_CHAR_LIMIT);
			} else {
				descriptionString = metrics[i].getDescription();
			}
			
			String rangeString;
			Object[] rangeArr = metrics[i].getRange();
			
			switch(metrics[i].getType()) {
				case DBContract.COUNTER:
					
					rangeString = "Min:" + rangeArr[0] + " Max:" + rangeArr[1] + 
						" Inc:" + rangeArr[2];
					break;
					
				case DBContract.CHOOSER:
					
					rangeString = new String();
					
					for(Object o : rangeArr) {
						rangeString += o + ", ";
					}
					
					break;
					
				case DBContract.SLIDER:
					
					rangeString = "Min:" + rangeArr[0] + " Max:" + rangeArr[1];
					
					break;
					
				case DBContract.MATH:
					
					rangeString = new String();
					
					for(Object o : rangeArr) {
						rangeString += o + ", ";
					}
					
					break;
					
				default:
					
					rangeString = new String();
			}
			
			table.addView(new MyTableRow(this, new View[] {
					editButton,
					new MyTextView(this, metrics[i].getMetricName(), 18),
					new MyTextView(this, descriptionString, 18),
					new MyTextView(this, rangeString, 18),
					new MyTextView(this, Boolean.toString(metrics[i].isDisplayed()), 18),
			}, color));
		}
	}
	
	public void onClick(View v) {
		
		Intent i;
		
		switch(v.getId()) {
			case R.id.addMetric:
				
				i = new Intent(this, AddMetricDialogActivity.class);
				i.putExtra(AddMetricDialogActivity.METRIC_CATEGORY_EXTRA, metricCategory);
				i.putExtra(AddMetricDialogActivity.GAME_NAME_EXTRA, 
						databaseValues[getAddressOfDatabaseKey(DBContract.COL_GAME_NAME)]);
				startActivity(i);
				
				break;
				
			case EDIT_BUTTON_ID:
				
				i = new Intent(this, EditMetricDialogActivity.class);
				i.putExtra(EditMetricDialogActivity.METRIC_CATEGORY_EXTRA, metricCategory);
				i.putExtra(EditMetricDialogActivity.METRIC_ID_EXTRA, (Integer)v.getTag());
				startActivity(i);
				
				break;
		}
	}
}
