package com.team2052.frckrawler.gui;

import android.content.Context;

import com.team2052.frckrawler.database.structures.MetricValue;

public class MathMetricWidget extends MetricWidget {
	
	public MathMetricWidget(Context c, MetricValue m) {
		super(c, m.getMetric(), new String[0]);
	}
	
	public String[] getValues() {
		// TODO Auto-generated method stub
		return new String[0];
	}

}
