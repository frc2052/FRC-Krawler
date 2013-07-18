package com.team2052.frckrawler;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
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
	public static final int DRIVER_METRICS = 3;	//Currently not used
	
	private static final int EDIT_BUTTON_ID = 1;
	private static final int SELECTED_BUTTON_ID = 2;
	private static final int DESCRIPTION_CHAR_LIMIT = 20;
	
	private boolean isGettingMetrics;
	private int metricCategory;	//Either MATCH_PERF_METRICS, ROBOT_METRICS, or DRIVER_METRICS
	private int selectedMetricID;
	private DBManager dbManager;
	private AbstractRadioGroup radioGroup;
	private TableLayout table;
	private Metric[] metrics;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_metrics);
		
		findViewById(R.id.addMetric).setOnClickListener(this);
		findViewById(R.id.up).setOnClickListener(this);
		findViewById(R.id.down).setOnClickListener(this);
		
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
		
		isGettingMetrics = false;
		dbManager = DBManager.getInstance(this);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		new GetMetricsTask().execute();
	}
	
	@Override
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
				
			case R.id.down:
				
				if(metrics.length == 0 || radioGroup.getSelectedButton() == null ||
						isGettingMetrics)
					break;
				else {
					int metricPos = 0;
					int metricID = ((Integer)radioGroup.getSelectedButton().getTag()).intValue();

					for(int k = 0; k < metrics.length; k++) {
						if(metrics[k].getID() == metricID)
							metricPos = k;
					}

					if(metricPos == metrics.length - 1)
						break;

					int lowerMetricPos = metricPos + 1;
					int lowerMetricID = metrics[lowerMetricPos].getID();

					if(metricCategory == MATCH_PERF_METRICS) {
						System.out.println(dbManager.flipMatchMetricPosition(metricID,lowerMetricID));

					} else if(metricCategory == ROBOT_METRICS) {
						dbManager.flipRobotMetricPosition(metricID, lowerMetricID);
					}
					
					new GetMetricsTask().execute();

					break;
				}
				
			case R.id.up:
				
				if(metrics.length == 0 || radioGroup.getSelectedButton() == null || 
						isGettingMetrics)
					break;
				else {
					int metricPos = 0;
					int metricID = ((Integer)radioGroup.getSelectedButton().getTag()).intValue();

					for(int k = 0; k < metrics.length; k++) {
						if(metrics[k].getID() == metricID)
							metricPos = k;
					}

					if(metricPos == 0)
						break;

					int upperMetricPos = metricPos - 1;
					int upperMetricID = metrics[upperMetricPos].getID();

					if(metricCategory == MATCH_PERF_METRICS) {
						dbManager.flipMatchMetricPosition(metricID, upperMetricID);

					} else if(metricCategory == ROBOT_METRICS) {
						dbManager.flipRobotMetricPosition(metricID, upperMetricID);
					}
					
					new GetMetricsTask().execute();

					break;
				}
				
			case SELECTED_BUTTON_ID:
				
				radioGroup.notifyClick((RadioButton)v);
				selectedMetricID = (Integer)v.getTag();
				
				break;
		}
	}
	
	private class GetMetricsTask extends AsyncTask<Void, MyTableRow, Void> {
		
		private int metricNum;
		
		@Override
		protected void onPreExecute() {
			metricNum = 0;
			isGettingMetrics = true;
			radioGroup = new AbstractRadioGroup();
			table = (TableLayout)findViewById(R.id.metricsDataTable);
			table.removeAllViews();
		}

		@Override
		protected Void doInBackground(Void... v) {
			switch(metricCategory) {
				case MATCH_PERF_METRICS:
					
					metrics = dbManager.getMatchPerformanceMetricsByColumns
						(new String[] {DBContract.COL_GAME_NAME}, 
							new String[] {databaseValues[MetricsActivity.this.getAddressOfDatabaseKey
							                             (DBContract.COL_GAME_NAME)]});
					
					break;
					
				case ROBOT_METRICS:
					
					metrics = dbManager.getRobotMetricsByColumns
							(new String[] {DBContract.COL_GAME_NAME}, 
									new String[] {databaseValues[MetricsActivity.this.getAddressOfDatabaseKey
									                             (DBContract.COL_GAME_NAME)]});
					
					break;
					
				case DRIVER_METRICS:
					
				default:
					
					metrics = new Metric[0];
			}
			
			MyTableRow descriptorsRow = new MyTableRow(MetricsActivity.this);
			descriptorsRow.addView(new MyTextView(MetricsActivity.this, " ", 18));
			descriptorsRow.addView(new MyTextView(MetricsActivity.this, " ", 18));
			descriptorsRow.addView(new MyTextView(MetricsActivity.this, "Name", 18));
			descriptorsRow.addView(new MyTextView(MetricsActivity.this, "Description", 18));
			descriptorsRow.addView(new MyTextView(MetricsActivity.this, "Type", 18));
			descriptorsRow.addView(new MyTextView(MetricsActivity.this, "Range", 18));
			descriptorsRow.addView(new MyTextView(MetricsActivity.this, "Displayed", 18));
			publishProgress(descriptorsRow);
			
			for(int i = 0; i < metrics.length; i++) {
				int color;
				
				if(i % 2 == 0)
					color = GlobalValues.ROW_COLOR;
				else
					color = Color.TRANSPARENT;
				
				RadioButton selectedButton = new RadioButton(MetricsActivity.this);
				selectedButton.setId(SELECTED_BUTTON_ID);
				selectedButton.setTag(Integer.valueOf(metrics[i].getID()));
				selectedButton.setOnClickListener(MetricsActivity.this);
				radioGroup.add(selectedButton);
				if(selectedMetricID == metrics[i].getID())
					radioGroup.selectButton(selectedButton);
				
				MyButton editButton = new MyButton(MetricsActivity.this, "Edit Metric", MetricsActivity.this, 
						Integer.valueOf(metrics[i].getID()));
				editButton.setId(EDIT_BUTTON_ID);
				editButton.setTag(metrics[i].getID());
				
				String descriptionString;
				
				if(metrics[i].getDescription().length() >= DESCRIPTION_CHAR_LIMIT) {
					descriptionString = metrics[i].getDescription().
							substring(0, DESCRIPTION_CHAR_LIMIT);
				} else {
					descriptionString = metrics[i].getDescription();
				}
				
				String rangeString = new String();
				String typeString = new String();
				Object[] rangeArr = metrics[i].getRange();
				
				switch(metrics[i].getType()) {
					case DBContract.BOOLEAN:
						typeString = "Boolean";
						break;
					
					case DBContract.TEXT:
						typeString = "Text";
						break;
					
					case DBContract.COUNTER:
						rangeString = "Min:" + rangeArr[0] + " Max:" + rangeArr[1] + 
							" Inc:" + rangeArr[2];
						
						typeString = "Counter";
						
						break;
						
					case DBContract.CHOOSER:
						
						rangeString = new String();
						
						for(Object o : rangeArr) {
							rangeString += o + ", ";
						}
						
						typeString = "Chooser";
						
						break;
						
					case DBContract.SLIDER:
						
						rangeString = "Min:" + rangeArr[0] + " Max:" + rangeArr[1];
						typeString = "Slider";
						
						break;
						
					case DBContract.MATH:
						
						rangeString = new String();
						
						for(Object o : rangeArr) {
							rangeString += o + ", ";
						}
						
						typeString = "Math";
						
						break;
						
					default:
						
						rangeString = new String();
				}
				
				MyTableRow row = new MyTableRow(MetricsActivity.this, new View[] {
						selectedButton,
						editButton,
						new MyTextView(MetricsActivity.this, metrics[i].getMetricName(), 18),
						new MyTextView(MetricsActivity.this, descriptionString, 18),
						new MyTextView(MetricsActivity.this, typeString, 18),
						new MyTextView(MetricsActivity.this, rangeString, 18),
						new MyTextView(MetricsActivity.this, 
								Boolean.toString(metrics[i].isDisplayed()), 18),
				}, color);
				
				publishProgress(row);
			}
			
			return null;
		}
		
		@Override
		protected void onProgressUpdate(MyTableRow... row) {
			table.addView(row[0]);
		}
		
		@Override
		protected void onPostExecute(Void v) {
			((TextView)findViewById(R.id.metricNum)).setText(metricNum + " Metrics");
			isGettingMetrics = false;
		}
	}
}
