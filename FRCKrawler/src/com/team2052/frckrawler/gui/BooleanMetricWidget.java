package com.team2052.frckrawler.gui;

import android.content.Context;
import android.view.View.OnClickListener;
import android.view.View;
import android.widget.*;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.database.structures.MetricValue;

public class BooleanMetricWidget extends MetricWidget implements OnClickListener {
	
	private boolean value;
	
	public BooleanMetricWidget(Context context, MetricValue m) {
		
		super(context, m.getMetric(), m.getValue());
		inflater.inflate(R.layout.widget_metric_boolean, this);
		((TextView)findViewById(R.id.name)).setText(m.getMetric().getMetricName());
		findViewById(R.id.yes).setOnClickListener(this);
		findViewById(R.id.no).setOnClickListener(this);
		
		if(m.getValue() != null && m.getValue().length > 0)
			value = Boolean.parseBoolean(m.getValue()[0]);
	}

	public String[] getValues() {
		
		return new String[] {Boolean.toString(value)};
	}

	public void onClick(View view) {
	    
	    boolean checked = ((RadioButton)view).isChecked();
	    
	    switch(view.getId()) {
	        case R.id.yes:
	            if (checked)
	                value = true;
	            	
	            break;
	            
	        case R.id.no:
	            if (checked)
	               value = false;
	            	
	            break;
	    }
	}
}
