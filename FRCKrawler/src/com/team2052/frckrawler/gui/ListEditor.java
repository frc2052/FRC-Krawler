package com.team2052.frckrawler.gui;

import java.util.ArrayList;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public abstract class ListEditor extends LinearLayout implements OnClickListener {
	
	private static final int ADD_BUTTON_ID = 1;
	private static final int REMOVE_BUTTON_ID = 2;
	
	private ArrayList<Object> values;
	private Button addButton;

	public ListEditor(Context context) {
		
		this(context, new Object[0]);
	}
	
	public ListEditor(Context context, Object[] list) {
		
		super(context);
		setOrientation(VERTICAL);
		
		values = new ArrayList<Object>();
		
		for(Object o : list)
			values.add(o);
		
		addButton = new MyButton(getContext(), "Add...", this);
		addButton.setId(ADD_BUTTON_ID);
		addButton.setGravity(Gravity.CENTER);
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
	
	public Object[] getValues() {
		
		return values.toArray();
	}
	
	public int indexOf(Object o) {
		
		return values.indexOf(o);
	}
	
	public int getValueCount() {
		
		return values.size();
	}
	
	public void addValue(String val) {
		
		values.add(val);
		onValuesUpdated();
	}
	
	public void removeValue(Object val) {
		
		values.remove(val);
		onValuesUpdated();
	}
	
	public void removeValue(int position) {
		
		values.remove(position);
		onValuesUpdated();
	}
	
	public void onValuesUpdated() {
		
		removeAllViews();
		
		for(int i = 0; i < values.size(); i ++) {
			
			LinearLayout l = new LinearLayout(getContext());
			l.setOrientation(LinearLayout.HORIZONTAL);
			
			TextView t = new TextView(getContext());
			t.setText(values.get(i).toString());
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
	
	public ArrayList<Object> getValuesList() {
		
		return values;
	}
	
	public void setEnabled(boolean enabled) {
		
		super.setEnabled(enabled);
		addButton.setEnabled(enabled);
	}
}
