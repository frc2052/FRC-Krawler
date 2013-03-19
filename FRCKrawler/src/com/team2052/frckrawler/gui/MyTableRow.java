package com.team2052.frckrawler.gui;

/*****
 * Class: MyTableRow
 * 
 * Summary: This class extends android's TableRow class to add few
 * extra constructors that make it easier to create many table rows,
 * especially in arrays.
 *****/

import android.R.color;
import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

public class MyTableRow extends TableRow {
	
	public MyTableRow(Context _context) {
		
		this(_context, new String[0]);
	}

	public MyTableRow(Context _context, String[] _vals) {
		
		this(_context, _vals, 18);
	}
	
	public MyTableRow(Context _context, String[] _vals, int _fontSize) {
		
		super(_context);
		
		for(String s : _vals) {
			
			TextView v = new TextView(_context);
			v.setText(s);
			v.setTextSize(TypedValue.COMPLEX_UNIT_SP, _fontSize);
			addView(v);
		}
	}
	
	public MyTableRow(Context _context, int _color) {
		
		super(_context);
		this.setBackgroundColor(_color);
	}
	
	public MyTableRow(Context _context, View[] _vals) {
		
		this(_context, _vals, color.transparent);
	}
	
	public MyTableRow(Context _context, View[] _vals, int _backgroundColor) {
		
		super(_context);
		
		for(View v : _vals) {
			
			addView(v);
		}
		
		setBackgroundColor(_backgroundColor);
	}
}
