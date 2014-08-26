package com.team2052.frckrawler.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.team2052.frckrawler.R;

public class DriverDataActivity extends StackableTabActivity implements OnClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_data);

        findViewById(R.id.addData).setOnClickListener(this);
    }

    @Override
    public void onResume() {

        super.onResume();

        TableLayout table = (TableLayout) findViewById(R.id.dataTable);
        TableRow descriptorsRow = new TableRow(this);

        table.removeAllViews();
        table.addView(descriptorsRow);
    }

    @Override
    public void onClick(View v) {


    }
}
