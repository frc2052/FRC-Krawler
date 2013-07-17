package com.team2052.frckrawler;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.team2052.frckrawler.R;

public class MetricSelectionActivity extends StackableTabActivity implements OnClickListener {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_metric_selection);
		
		findViewById(R.id.matchPerfMetrics).setOnClickListener(this);
		findViewById(R.id.robotMetrics).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		
		Intent i = new Intent(this, MetricsActivity.class);
		
		switch(v.getId()) {
			case R.id.matchPerfMetrics:
				
				i.putExtra(MetricsActivity.METRIC_CATEGORY_EXTRA, 
						MetricsActivity.MATCH_PERF_METRICS);
				
				break;
				
			case R.id.robotMetrics:
				
				i.putExtra(MetricsActivity.METRIC_CATEGORY_EXTRA, 
						MetricsActivity.ROBOT_METRICS);
				
				break;
				
			/*case R.id.driverMetrics:
				
				i.putExtra(MetricsActivity.METRIC_CATEGORY_EXTRA, 
						MetricsActivity.DRIVER_METRICS);
				
				break;*/
				
			default:
				
				i.putExtra(MetricsActivity.METRIC_CATEGORY_EXTRA, 
						MetricsActivity.MATCH_PERF_METRICS);	
		}
		
		i.putExtra(PARENTS_EXTRA, parents);
		i.putExtra(DB_VALUES_EXTRA, databaseValues);
		i.putExtra(DB_KEYS_EXTRA, databaseKeys);
		startActivity(i);
	}

}
