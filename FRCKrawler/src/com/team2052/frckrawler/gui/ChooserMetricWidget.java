package com.team2052.frckrawler.gui;

import android.content.Context;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.database.structures.MetricValue;

public class ChooserMetricWidget extends MetricWidget implements OnItemSelectedListener {
	
	private String value;
	
	public ChooserMetricWidget(Context context, MetricValue m) {
		
		super(context, m.getMetric(), m.getValue());
		inflater.inflate(R.layout.widget_metric_chooser, this);
		
		if(m.getValue() != null && m.getValue().length > 0)
			value = m.getValue()[0];
		
		ArrayAdapter<Object> adapter = new ArrayAdapter<Object>(getContext(), 
				android.R.layout.simple_spinner_item);
		
		for(Object s : m.getMetric().getRange()) 
			adapter.add(s);
		
		((Spinner)findViewById(R.id.choooserList)).setAdapter(adapter);
		((Spinner)findViewById(R.id.choooserList)).setOnItemSelectedListener(this);
		((TextView)findViewById(R.id.name)).setText(m.getMetric().getMetricName());
	}

	public String[] getValues() {
		
		return new String[] {value};
	}

	public void onItemSelected(AdapterView<?> a, View arg1, int arg2,
			long arg3) {
		
		value = (String)a.getSelectedItem();
	}

	public void onNothingSelected(AdapterView<?> a) {
		
		a.setSelection(0);
	}
}
