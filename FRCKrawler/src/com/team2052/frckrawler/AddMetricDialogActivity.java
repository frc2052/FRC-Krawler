package com.team2052.frckrawler;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;

import com.example.frckrawler.R;
import com.team2052.frckrawler.database.*;
import com.team2052.frckrawler.database.structures.Metric;
import com.team2052.frckrawler.gui.*;

public class AddMetricDialogActivity extends Activity 
					implements OnClickListener, OnItemSelectedListener {
	
	public static final String GAME_NAME_EXTRA = "com.team2052.frckrawler.gameNameExtra";
	public static final String METRIC_CATEGORY_EXTRA = "com.team2052.frckrawler.categoryExtra";
	
	private int metricCategory;
	private int selectedMetricType;
	
	private ListEditor list;
	
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialogactivity_add_metric);
		
		list = new TextListEditor(this);
		((RelativeLayout)findViewById(R.id.listEditorSlot)).addView(list);
		
		findViewById(R.id.add).setOnClickListener(this);
		findViewById(R.id.cancel).setOnClickListener(this);
		
		((Spinner)findViewById(R.id.type)).setOnItemSelectedListener(this);
		((Spinner)findViewById(R.id.type)).setSelection(0);
		
		metricCategory = getIntent().getIntExtra(METRIC_CATEGORY_EXTRA, 1);
		selectedMetricType = 0;
		
		refreshTypeBasedUI();
	}

	public void onClick(View v) {
		
		if(v.getId() == R.id.add) {
			
			Metric m = null;
			
			switch(selectedMetricType) {
				case DBContract.BOOLEAN:
					
					m = Metric.MetricFactory.createBooleanMetric(
							getIntent().getStringExtra(GAME_NAME_EXTRA),
							((EditText)findViewById(R.id.name)).getText().toString(),
							((EditText)findViewById(R.id.description)).getText().toString(),
							((CheckBox)findViewById(R.id.displayed)).isChecked()
							);
					break;
					
				case DBContract.COUNTER:
					
					try{
						m = Metric.MetricFactory.createCounterMetric(
							getIntent().getStringExtra(GAME_NAME_EXTRA),
							((EditText)findViewById(R.id.name)).getText().toString(),
							((EditText)findViewById(R.id.description)).getText().toString(),
							Integer.parseInt(((EditText)findViewById(R.id.min)).getText().toString()), 
							Integer.parseInt(((EditText)findViewById(R.id.max)).getText().toString()),
							Integer.parseInt(((EditText)findViewById(R.id.inc)).getText().toString()),
							((CheckBox)findViewById(R.id.displayed)).isChecked()
							);
					} catch(NumberFormatException e) {
						
						Toast.makeText(this, "Could not create metric. Make sure you " +
								"have filled out all of the necessary fields.", 
								Toast.LENGTH_SHORT).show();
						return;
					}
					
					break;
					
				case DBContract.SLIDER:
					
					try {
						m = Metric.MetricFactory.createSliderMetric(
							getIntent().getStringExtra(GAME_NAME_EXTRA),
							((EditText)findViewById(R.id.name)).getText().toString(),
							((EditText)findViewById(R.id.description)).getText().toString(),
							Integer.parseInt(((EditText)findViewById(R.id.min)).getText().toString()), 
							Integer.parseInt(((EditText)findViewById(R.id.max)).getText().toString()),
							((CheckBox)findViewById(R.id.displayed)).isChecked()
							);
					} catch(NumberFormatException e) {
				
						Toast.makeText(this, "Could not create metric. Make sure you " +
								"have filled out all of the necessary fields.", 
								Toast.LENGTH_SHORT).show();
						return;
					}
					
					break;
					
				case DBContract.CHOOSER:
					
					m = Metric.MetricFactory.createChooserMetric(
							getIntent().getStringExtra(GAME_NAME_EXTRA),
							((EditText)findViewById(R.id.name)).getText().toString(),
							((EditText)findViewById(R.id.description)).getText().toString(),
							list.getValues(),
							((CheckBox)findViewById(R.id.displayed)).isChecked()
							);
					
					for(String s : list.getValues())
						System.out.println(s);
					
					break;
					
				case DBContract.TEXT:
					
					m = Metric.MetricFactory.createTextMetric(
							getIntent().getStringExtra(GAME_NAME_EXTRA),
							((EditText)findViewById(R.id.name)).getText().toString(),
							((EditText)findViewById(R.id.description)).getText().toString(),
							((CheckBox)findViewById(R.id.displayed)).isChecked()
							);
					
					break;
					
				case DBContract.MATH:
					
					String[] selectedMetrics = list.getValues();
					Integer[] selectedMetricIDs = new Integer[selectedMetrics.length];
					
					for(int i = 0; i < selectedMetrics.length; i++)
						selectedMetricIDs[i] = Integer.valueOf(selectedMetrics[i]);
					
					m = Metric.MetricFactory.createMathMetric(
							getIntent().getStringExtra(GAME_NAME_EXTRA),
							((EditText)findViewById(R.id.name)).getText().toString(),
							((EditText)findViewById(R.id.description)).getText().toString(),
							selectedMetricIDs,
							((CheckBox)findViewById(R.id.displayed)).isChecked()
							);
					
					break;
					
				default:
					
					m = Metric.MetricFactory.createBooleanMetric(
							getIntent().getStringExtra(GAME_NAME_EXTRA),
							((EditText)findViewById(R.id.name)).getText().toString(),
							((EditText)findViewById(R.id.description)).getText().toString(),
							((CheckBox)findViewById(R.id.displayed)).isChecked()
							);
			}
			
			boolean added = false;
			
			DBManager db = DBManager.getInstance(this);
			
			switch(metricCategory) {
				case MetricsActivity.MATCH_PERF_METRICS:
					added = db.addMatchPerformanceMetric(m);
					break;
					
				case MetricsActivity.ROBOT_METRICS:
					added = db.addRobotMetric(m);
					break;
					
				case MetricsActivity.DRIVER_METRICS:
					added = db.addDriverMetric(m);
					break;
			}
			
			if(added) {
				
				finish();
				
			} else {
				
				
			}
			
		} else if(v.getId() == R.id.cancel) {
			
			finish();
		}
	}

	public void onItemSelected(AdapterView<?> parent, View v, int pos,
			long id) {
		
		selectedMetricType = pos;
		
		refreshTypeBasedUI();
	}

	public void onNothingSelected(AdapterView<?> v) {
		
		((Spinner)findViewById(R.id.game)).setSelection(0);
		
		refreshTypeBasedUI();
	}
	
	protected void refreshTypeBasedUI() {
		
		if(selectedMetricType == DBContract.COUNTER) {
			
			findViewById(R.id.min).setEnabled(true);
			findViewById(R.id.max).setEnabled(true);
			findViewById(R.id.inc).setEnabled(true);
			list.setEnabled(false);
			
		} else if(selectedMetricType == DBContract.SLIDER) {
			
			findViewById(R.id.min).setEnabled(true);
			findViewById(R.id.max).setEnabled(true);
			findViewById(R.id.inc).setEnabled(false);
			list.setEnabled(false);
			
		} else if(selectedMetricType == DBContract.MATH) {
			
			DBManager db = DBManager.getInstance(this);
			Metric[] choices;
			
			switch(metricCategory) {
				case MetricsActivity.MATCH_PERF_METRICS:
					choices = db.getMatchPerformanceMetricsByColumns
						(new String[] {DBContract.COL_GAME_NAME}, 
								new String[] {getIntent().getStringExtra(GAME_NAME_EXTRA)});
					break;
				
				case MetricsActivity.ROBOT_METRICS:
					choices = db.getRobotMetricsByColumns
					(new String[] {DBContract.COL_GAME_NAME}, 
							new String[] {getIntent().getStringExtra(GAME_NAME_EXTRA)});
					break;
				
				case MetricsActivity.DRIVER_METRICS:
					/*choices = db.getDriverMetricsByColumns
					(new String[] {DBContract.COL_GAME_NAME}, 
							new String[] {getIntent().getStringExtra(GAME_NAME_EXTRA)});
					break;*/
					
				default:
					
					choices = new Metric[0];
			}
			
			findViewById(R.id.min).setEnabled(false);
			findViewById(R.id.max).setEnabled(false);
			findViewById(R.id.inc).setEnabled(false);
			list = new MathMetricListEditor(this, new String[0], choices);
			list.setEnabled(true);
			
		} else if(selectedMetricType == DBContract.CHOOSER) {
			
			findViewById(R.id.min).setEnabled(false);
			findViewById(R.id.max).setEnabled(false);
			findViewById(R.id.inc).setEnabled(false);
			list = new TextListEditor(this);
			list.setEnabled(true);
			
		} else {
			
			findViewById(R.id.min).setEnabled(false);
			findViewById(R.id.max).setEnabled(false);
			findViewById(R.id.inc).setEnabled(false);
			list.setEnabled(false);
		}
	}
}
