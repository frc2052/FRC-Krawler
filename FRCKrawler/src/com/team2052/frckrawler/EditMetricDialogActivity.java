package com.team2052.frckrawler;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.team2052.frckrawler.database.DBContract;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.Metric;

public class EditMetricDialogActivity extends Activity implements OnClickListener {
	
	public static final String METRIC_ID_EXTRA = "com.team2052.frckrawler.metricIDExtra";
	public static final String METRIC_CATEGORY_EXTRA = "com.team2052.frckrawler.categoryExtra";
	
	private int metricCategory;
	private Metric metric;
	private DBManager db;
	
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialogactivity_edit_metric);
		findViewById(R.id.cancel).setOnClickListener(this);
		findViewById(R.id.remove).setOnClickListener(this);
		
		metricCategory = getIntent().getIntExtra(METRIC_CATEGORY_EXTRA, 1);
		
		db = DBManager.getInstance(this);
		
		switch(metricCategory) {
			case MetricsActivity.MATCH_PERF_METRICS:
				
				Metric[] arr = db.getMatchPerformanceMetricsByColumns
						(new String[] {DBContract.COL_METRIC_ID}, 
								new String[] {Integer.toString
								(getIntent().getIntExtra(METRIC_ID_EXTRA, -1))});
				
				if(arr.length > 0)
					metric = arr[0];
				
				break;
				
			case MetricsActivity.ROBOT_METRICS:
				
				Metric[] rarr = db.getMatchPerformanceMetricsByColumns
				(new String[] {DBContract.COL_METRIC_ID}, 
						new String[] {Integer.toString
						(getIntent().getIntExtra(METRIC_ID_EXTRA, -1))});
		
				if(rarr.length > 0)
					metric = rarr[0];
		
			break;
		}
		
		if(metric != null) {
			String[] metricTypes = getResources().getStringArray(R.array.metric_types);
			((TextView)findViewById(R.id.type)).setText(metricTypes[metric.getType()]);
			((EditText)findViewById(R.id.name)).setText(metric.getMetricName());
			((EditText)findViewById(R.id.description)).setText(metric.getDescription());
			((CheckBox)findViewById(R.id.displayed)).setChecked(metric.isDisplayed());
			
			Object[] range = metric.getRange();
			
			switch(metric.getType()) {
				case DBContract.TEXT:
				case DBContract.BOOLEAN:
					
					((EditText)findViewById(R.id.min)).setEnabled(false);
					((EditText)findViewById(R.id.max)).setEnabled(false);
					((EditText)findViewById(R.id.inc)).setEnabled(false);
					
					break;
					
				case DBContract.COUNTER:
					
					((EditText)findViewById(R.id.min)).setText((String)range[0]);
					((EditText)findViewById(R.id.max)).setText((String)range[1]);
					((EditText)findViewById(R.id.inc)).setText((String)range[2]);
					
					break;
					
				case DBContract.SLIDER:
					
					((EditText)findViewById(R.id.min)).setText((String)range[0]);
					((EditText)findViewById(R.id.max)).setText((String)range[1]);
					((EditText)findViewById(R.id.inc)).setEnabled(false);
					
					break;
			}
		}
	}

	public void onClick(View v) {
		
		DBManager db = DBManager.getInstance(this);
		
		switch(v.getId()) {
			case R.id.cancel:
				
				finish();
				
				break;
				
			case R.id.remove:
				
				switch(metricCategory) {
					case MetricsActivity.MATCH_PERF_METRICS:
						
						db.removeMatchPerformaceMetric
								(getIntent().getIntExtra(METRIC_ID_EXTRA, -1));
						
						break;
						
					case MetricsActivity.ROBOT_METRICS:
						
						db.removeRobotMetric(getIntent().getIntExtra
								(METRIC_ID_EXTRA, -1));
						
						break;
						
					case MetricsActivity.DRIVER_METRICS:
						
						db.removeDriverMetric(getIntent().getIntExtra
								(METRIC_ID_EXTRA, -1));
						
						break;
				}
				
				finish();
				
				break;
				
			case R.id.saveMetric:
				
				switch(metricCategory) {
					case MetricsActivity.MATCH_PERF_METRICS:
						
						
						
						break;
						
					case MetricsActivity.ROBOT_METRICS:
					
						
					
						break;
					
					case MetricsActivity.DRIVER_METRICS:
					
						
					
						break;
				}
				
				finish();
				
				break;
		}
	}
}
