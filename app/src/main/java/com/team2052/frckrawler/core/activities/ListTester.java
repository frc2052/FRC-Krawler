package com.team2052.frckrawler.core.activities;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.core.ui.ListEditor;
import com.team2052.frckrawler.core.ui.TextListEditor;

import java.util.List;

public class ListTester extends BaseActivity implements OnClickListener {

    ListEditor list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_tester);
        list = new TextListEditor(this);
        ((FrameLayout) findViewById(R.id.listContainer)).addView(list);
        findViewById(R.id.print).setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        list = new TextListEditor(this);
        ((FrameLayout) findViewById(R.id.listContainer)).removeAllViews();
        ((FrameLayout) findViewById(R.id.listContainer)).addView(list);
    }

    @Override
    public void onClick(View v) {
        List<String> s = list.getValues();

        for (String g : s)
            System.out.println(g);
    }
}
