package com.team2052.frckrawler.gui;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;

public class StaticRowTableLayout extends LinearLayout {
	
	private TableLayout staticTable;
	private TableLayout mainTable;
	private ScrollView mainTableScroller;
	
	public StaticRowTableLayout(Context context) {
		super(context);
		
		setOrientation(HORIZONTAL);
		
		staticTable = new TableLayout(context);
		mainTable = new TableLayout(context);
		mainTableScroller = new ScrollView(context);
		
		addView(staticTable);
		mainTableScroller.addView(mainTable);
		addView(mainTableScroller);
	}
	
	public StaticRowTableLayout(Context context, TableRow staticRow) {
		this(context);
		
		staticTable.addView(staticRow);
	}
	
	public void setStaticRow(TableRow row) {
		staticTable.removeAllViews();
		staticTable.addView(row);
	}
	
	public void removeStaticRow() {
		staticTable.removeAllViews();
	}
	
	public void addView(View v) {
		mainTable.addView(v);
	}
	
	public void removeAllViews() {
		mainTable.removeAllViews();
	}
}
