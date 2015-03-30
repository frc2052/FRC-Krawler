package com.team2052.frckrawler.core.activities;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.core.ui.ListEditor;

public class ListTester extends BaseActivity {

    ListEditor list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_tester);
        list = new ListEditor(this);
        ((FrameLayout) findViewById(R.id.listContainer)).addView(list);
    }
}
