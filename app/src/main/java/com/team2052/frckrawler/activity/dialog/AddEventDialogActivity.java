package com.team2052.frckrawler.activity.dialog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activity.NewDatabaseActivity;
import com.team2052.frckrawler.database.models.Event;
import com.team2052.frckrawler.database.models.Game;

import java.util.GregorianCalendar;

public class AddEventDialogActivity extends NewDatabaseActivity implements OnClickListener {

    private Game mGame;

    public static Intent newInstance(Context c, Game game) {
        Intent i = new Intent(c, AddEventDialogActivity.class);
        i.putExtra(PARENT_ID, game.getId());
        return i;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialogactivity_add_event);
        mGame = Game.load(Game.class, getIntent().getLongExtra(PARENT_ID, -1));
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

            new Event(
                    ((EditText) findViewById(R.id.eventName)).getText().toString(),
                    mGame,
                    new GregorianCalendar(year, month, day, 0, 0).getTime().toString(),
                    ((EditText) findViewById(R.id.location)).getText().toString(),
                    ((EditText) findViewById(R.id.fmsID)).getText().toString()).save();

            finish();

        } else if (v.getId() == R.id.cancel) {

            finish();
        }
    }
}
