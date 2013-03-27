package com.team2052.frckrawler;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;

import com.team2052.frckrawler.gui.ListEditor;
import com.team2052.frckrawler.gui.TextListEditor;

public class ListTester extends Activity implements OnClickListener {
	
	ListEditor list;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_tester);
		list = new TextListEditor(this);
		((FrameLayout)findViewById(R.id.listContainer)).addView(list);
		findViewById(R.id.print).setOnClickListener(this);
	}
	
	public void onResume() {
		super.onResume();
		list = new TextListEditor(this);
		((FrameLayout)findViewById(R.id.listContainer)).removeAllViews();
		((FrameLayout)findViewById(R.id.listContainer)).addView(list);
	}

	@Override
	public void onClick(View v) {
		String[] s = list.getValues();
		
		for(String g : s)
			System.out.println(g);
	}
}
