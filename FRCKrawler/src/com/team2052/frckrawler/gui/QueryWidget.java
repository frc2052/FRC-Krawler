package com.team2052.frckrawler.gui;

import android.content.Context;
import android.widget.LinearLayout;

import com.team2052.frckrawler.database.structures.Metric;
import com.team2052.frckrawler.database.structures.Query;

public class QueryWidget extends LinearLayout {
	
	Metric[] metrics;
	
	public QueryWidget(Context _context, Metric[] _metrics) {
		
		super(_context);
		metrics = _metrics;
	}
	
	public Query[] getQuerys() {
		
		return new Query[0];
	}
}
