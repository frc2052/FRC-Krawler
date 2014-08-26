package com.team2052.frckrawler.activity.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.database.DBContract;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.MatchData;
import com.team2052.frckrawler.database.structures.Metric;
import com.team2052.frckrawler.database.structures.MetricValue;
import com.team2052.frckrawler.database.structures.Robot;
import com.team2052.frckrawler.gui.MetricWidget;

public class AddMatchDataDialogActivity extends Activity implements OnClickListener {

    public static String EVENT_ID_EXTRA = "com.team2052.frckrawler.eventIDExtra";
    public static String GAME_NAME_EXTRA = "com.team2052.frckrawler.gameNameExtra";

    private DBManager db;
    private Robot[] robotChoices;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode
                (WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.dialogactivity_add_match_data);

        findViewById(R.id.addData).setOnClickListener(this);
        findViewById(R.id.cancel).setOnClickListener(this);
        ((EditText) findViewById(R.id.matchNumber)).
                setInputType(InputType.TYPE_CLASS_NUMBER);

        //Set the ScrollView to not auto scroll to EditText
        ScrollView sv = (ScrollView) findViewById(R.id.scrollView1);
        sv.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
        sv.setFocusable(true);
        sv.setFocusableInTouchMode(true);
        sv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.requestFocusFromTouch();
                return false;
            }
        });

        db = DBManager.getInstance(this);
    }

    @Override
    public void onResume() {

        super.onResume();

        //Add the list of robots to the robot spinner
        robotChoices = db.getRobotsAtEvent
                (getIntent().getIntExtra(EVENT_ID_EXTRA, -1));
        String[] robotTeams = new String[robotChoices.length];

        for (int i = 0; i < robotChoices.length; i++)
            robotTeams[i] = Integer.toString(robotChoices[i].getTeamNumber());

        Spinner robotSpinner = (Spinner) findViewById(R.id.robot);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, robotTeams);

        robotSpinner.setAdapter(adapter);

        //Add the metrics to the UI
        Metric[] metrics = db.getMatchPerformanceMetricsByColumns
                (new String[]{DBContract.COL_GAME_NAME},
                        new String[]{getIntent().getStringExtra(GAME_NAME_EXTRA)});

        LinearLayout metricWidgetList = (LinearLayout) findViewById(R.id.metricWidgetList);
        metricWidgetList.removeAllViews();

        for (int metricCount = 0; metricCount < metrics.length; metricCount++) {
            metricWidgetList.addView(MetricWidget.createWidget(this, metrics[metricCount]));
        }
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.addData) {

            if (((Spinner) findViewById(R.id.robot)).getSelectedItem() == null) {

                Toast.makeText(this, "Match data entry not created. Either you did not select " +
                        "a team, or there are no teams at this event.", Toast.LENGTH_LONG).show();
                return;
            }


            try {
                LinearLayout metricList = (LinearLayout) findViewById(R.id.metricWidgetList);
                MetricValue[] metricVals = new MetricValue[metricList.getChildCount()];

                for (int widgetCount = 0; widgetCount < metricList.getChildCount(); widgetCount++) {

                    MetricWidget widget = (MetricWidget) metricList.getChildAt(widgetCount);
                    metricVals[widgetCount] = widget.getMetricValue();
                }

                db.insertMatchData(new MatchData(
                        getIntent().getIntExtra(EVENT_ID_EXTRA, -1),
                        Integer.parseInt(((EditText) findViewById(R.id.matchNumber)).
                                getText().toString()),
                        robotChoices[((Spinner) findViewById(R.id.robot)).
                                getSelectedItemPosition()].getID(),
                        -1,
                        ((Spinner) findViewById(R.id.gameType)).getSelectedItem().toString(),
                        ((EditText) findViewById(R.id.comments)).getText().toString(),
                        metricVals
                ));

                setResult(RESULT_OK);
                finish();

            } catch (NumberFormatException e) {

                Toast.makeText(this, "Data not added to the database. You must specify " +
                        "a match number.", Toast.LENGTH_SHORT).show();
            }

        } else {

            finish();
        }
    }
}
