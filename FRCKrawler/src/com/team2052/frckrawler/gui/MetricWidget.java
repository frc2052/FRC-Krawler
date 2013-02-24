package com.team2052.frckrawler.gui;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.*;

import com.example.frckrawler.R;
import com.team2052.frckrawler.database.structures.Metric;

public abstract class MetricWidget extends FrameLayout {
	
	protected LayoutInflater inflater;
	
	private String[] values;
	private Metric metric;
	
	protected MetricWidget(Context context, Metric m, String[] val) {
		
		super(context);
		values = val;
		metric = m;
		inflater = (LayoutInflater)context.getSystemService
				(Context.LAYOUT_INFLATER_SERVICE);
		
		((TextView)findViewById(R.id.name)).setText(m.getMetricName());
	}
	
	public abstract String[] getValues();
}
