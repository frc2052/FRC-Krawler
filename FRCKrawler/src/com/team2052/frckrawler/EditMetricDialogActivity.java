package com.team2052.frckrawler;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.database.DBManager;

public class EditMetricDialogActivity extends Activity implements OnClickListener {
	
	public static final String METRIC_ID_EXTRA = "com.team2052.frckrawler.metricIDExtra";
	public static final String METRIC_CATEGORY_EXTRA = "com.team2052.frckrawler.categoryExtra";
	
	private int metricCategory;
	
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialogactivity_edit_metric);
		
		findViewById(R.id.cancel).setOnClickListener(this);
		findViewById(R.id.remove).setOnClickListener(this);
		
		metricCategory = getIntent().getIntExtra(METRIC_CATEGORY_EXTRA, 1);
	}

	public void onClick(View v) {
		
		switch(v.getId()) {
			case R.id.cancel:
				
				finish();
				
				break;
				
			case R.id.remove:
				
				DBManager db = DBManager.getInstance(this);
				
				switch(metricCategory) {
					case MetricsActivity.MATCH_PERF_METRICS:
						
						System.out.println(db.removeMatchPerformaceMetric(getIntent().getIntExtra
								(METRIC_ID_EXTRA, -1)));
						
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
		}
	}
}
