package com.team2052.frckrawler.activity.dialog;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activity.BaseActivity;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.Event;

import java.util.GregorianCalendar;

public class AddEventDialogActivity extends BaseActivity implements OnClickListener {

    public static final String GAME_NAME_EXTRA = "com.team2052.frckrawler.gameNameExtra";

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialogactivity_add_event);

        findViewById(R.id.addEvent).setOnClickListener(this);
        findViewById(R.id.cancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.addEvent) {

            DatePicker date = (DatePicker) findViewById(R.id.date);

            int day = date.getDayOfMonth();
            int month = date.getMonth();
            int year = date.getYear();

            DBManager.getInstance(this).addEvent(new Event(
                    ((EditText) findViewById(R.id.eventName)).getText().toString(),
                    getIntent().getStringExtra(GAME_NAME_EXTRA),
                    new GregorianCalendar(year, month, day, 0, 0).getTime(),
                    ((EditText) findViewById(R.id.location)).getText().toString(),
                    ((EditText) findViewById(R.id.fmsID)).getText().toString()
            ));

            finish();

        } else if (v.getId() == R.id.cancel) {

            finish();
        }
    }
}
