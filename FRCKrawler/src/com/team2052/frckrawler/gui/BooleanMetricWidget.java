package com.team2052.frckrawler.gui;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.example.frckrawler.R;
import com.team2052.frckrawler.database.structures.MetricValue;

public class BooleanMetricWidget extends MetricWidget implements OnClickListener{
	
	private int max;
	private int min;
	private int increment;
	private int currentValue;
	
	public BooleanMetricWidget(Context context, MetricValue m) {
		
		super(context, m.getMetric(), m.getValue());
		inflater.inflate(R.layout.widget_metric_boolean, this);
		
		findViewById(R.id.plus).setOnClickListener(this);
		findViewById(R.id.minus).setOnClickListener(this);
		
		Object[] o = m.getMetric().getRange();
		
		max = (Integer)o[0];
		min = (Integer)o[1];
		increment = (Integer)o[2];
		
		if(m.getValue() != null)
			currentValue = Integer.parseInt(m.getValue()[0]);
	}

	public String[] getValues() {
		
		return new String[] {Integer.toString(currentValue)};
	}

	public void onClick(View v) {
		
		if(v.getId() == R.id.plus) {
			
			currentValue += increment;
			
			if(currentValue > max)
				currentValue = max;
			
		} else if(v.getId() == R.id.minus) {
			
			currentValue -= increment;
			
			if(currentValue < min)
				currentValue = min;
		}
		
		((TextView)findViewById(R.id.value)).setText(Integer.toString(currentValue));
	}
}
