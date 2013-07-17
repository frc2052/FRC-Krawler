package com.team2052.frckrawler.gui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TableLayout;

import com.team2052.frckrawler.R;

/*****
 * Class: StaticTableView
 * 
 * @author Charles Hofer
 *
 * Description: A table that allows for both vertical and horizontal scrolling, but also allows
 * "static" columns on the left side. Table rows are either added to the main table by calling
 * the regular addView methods or the addViewToMainTable, or to the static table that does
 * not scrol horizontally by calling addViewToStaticTable.
 *****/

public class StaticTableLayout extends FrameLayout {
	
	TableLayout mainTable;
	TableLayout staticSideTable;

	public StaticTableLayout(Context context) {
		super(context);
		inflate(context, R.layout.view_static_table_layout, this);
		mainTable = (TableLayout)findViewById(R.id.mainTable);
		staticSideTable = (TableLayout)findViewById(R.id.staticSideTable);
	}
	
	public StaticTableLayout(Context context, AttributeSet set) {
		super(context, set);
		inflate(context, R.layout.view_static_table_layout, this);
		mainTable = (TableLayout)findViewById(R.id.mainTable);
		staticSideTable = (TableLayout)findViewById(R.id.staticSideTable);
	}
	
	@Override
	public void addView(View child) {
		mainTable.addView(child);
	}
	
	@Override
	public void addView(View child, int pos) {
		mainTable.addView(child, pos);
	}
	
	public void addViewToStaticTable(View child) {
		staticSideTable.addView(child);
	}
	
	public void addViewToMainTable(View child) {
		addView(child);
	}
	
	@Override
	public void removeAllViews() {
		staticSideTable.removeAllViews();
		mainTable.removeAllViews();
	}
}
