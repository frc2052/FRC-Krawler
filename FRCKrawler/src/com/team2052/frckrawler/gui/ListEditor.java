package com.team2052.frckrawler.gui;

import java.util.ArrayList;

import android.content.Context;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;

public abstract class ListEditor extends LinearLayout implements OnClickListener {
	
	private static final int ADD_BUTTON_ID = 1;
	private static final int REMOVE_BUTTON_ID = 2;
	
	protected ArrayList<String> shownValues;
	protected ArrayList<String> values;
	
	private Button addButton;

	public ListEditor(Context context) {
		
		this(context, new String[0]);
	}
	
	public ListEditor(Context context, String[] list) {
		
		super(context);
		setOrientation(VERTICAL);
		
		shownValues = new ArrayList<String>();
		values = new ArrayList<String>();
		
		for(String o : list)
			values.add(o);
		
		addButton = new MyButton(getContext(), "Add...", this);
		addButton.setId(ADD_BUTTON_ID);
		addButton.setGravity(Gravity.CENTER);
		addButton.setOnClickListener(this);
		
		onValuesUpdated();
	}
	
	protected abstract void onAddButtonClicked();
	
	public void onClick(View v) {
		
		if(isEnabled()) {
			if(v.getId() == ADD_BUTTON_ID) {
			
				onAddButtonClicked();
			
			} else if(v.getId() == REMOVE_BUTTON_ID) {
			
				removeValue(((Integer)v.getTag()).intValue());
			}
		}
	}
	
	public String[] getShownValues() {
		
		return shownValues.toArray(new String[0]);
	}
	
	public String[] getValues() {
		
		for(int i = 0; i < values.size(); i++)
			System.out.println(values.get(i));
		
		return values.toArray(new String[0]);
	}
	
	public int getValueCount() {
		
		return values.size();
	}
	
	public void addValue(String val, String shownVal) {
		
		shownValues.add(shownVal);
		values.add(val);
		onValuesUpdated();
	}
	
	public void removeValue(String val) {
		
		shownValues.remove(values.indexOf(val));
		values.remove(val);
		onValuesUpdated();
	}
	
	public void removeValue(int position) {
		
		shownValues.remove(position);
		values.remove(position);
		onValuesUpdated();
	}
	
	public void onValuesUpdated() {
		
		removeAllViews();
		
		for(int i = 0; i < shownValues.size(); i ++) {
			
			LinearLayout l = new LinearLayout(getContext());
			l.setOrientation(LinearLayout.HORIZONTAL);
			
			TextView t = new TextView(getContext());
			t.setText(shownValues.get(i));
			t.setTextSize(18);
			
			Button b = new Button(getContext());
			b.setText("Remove");
			b.setOnClickListener(this);
			b.setId(REMOVE_BUTTON_ID);
			b.setTag(Integer.valueOf(i));
			
			l.addView(t);
			l.addView(b);
			addView(l);
		}
		
		addView(addButton);
	}
	
	public ArrayList<String> getValuesList() {
		
		return values;
	}
	
	public void setEnabled(boolean enabled) {
		
		super.setEnabled(true);
		addButton.setEnabled(true);
	}
}
