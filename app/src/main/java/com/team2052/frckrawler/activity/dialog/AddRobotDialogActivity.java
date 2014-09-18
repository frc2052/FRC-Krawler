package com.team2052.frckrawler.activity.dialog;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activity.NewDatabaseActivity;
import com.team2052.frckrawler.database.MetricValue;
import com.team2052.frckrawler.database.MetricValue.MetricTypeMismatchException;
import com.team2052.frckrawler.database.models.Game;
import com.team2052.frckrawler.gui.MetricWidget;

import java.util.List;

public class AddRobotDialogActivity extends NewDatabaseActivity implements OnClickListener, OnItemSelectedListener {

    public static final String TEAM_NUMBER_EXTRA = "com.team2052.frckrawler.teamNumberExtra";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.dialogactivity_add_robot);
        findViewById(R.id.cancel).setOnClickListener(this);
        findViewById(R.id.addRobot).setOnClickListener(this);
        ((Spinner) findViewById(R.id.gameSpinner)).setOnItemSelectedListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        //Set the choices for the spinner
        List<Game> games = new Select().from(Game.class).execute();
        String[] spinnerVals = new String[games.size()];

        for (int i = 0; i < spinnerVals.length; i++) {
            spinnerVals[i] = games.get(i).name;
        }

        Spinner gameSpinner = (Spinner) findViewById(R.id.gameSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerVals);
        gameSpinner.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addRobot:

                if (((Spinner) findViewById(R.id.gameSpinner)).getChildCount() == 0) {
                    Toast.makeText(this, "Could not add robot. " +
                            "There are no games in the database. " +
                            "Please create a game first.", Toast.LENGTH_LONG);
                }

                LinearLayout metricList = (LinearLayout) findViewById(R.id.metricList);
                MetricValue[] vals = new MetricValue[metricList.getChildCount()];

                for (int i = 0; i < metricList.getChildCount(); i++) {
                    try {
                        MetricWidget widget = (MetricWidget) metricList.getChildAt(i);
                        vals[i] = new MetricValue(widget.getMetric(), widget.getValues());

                    } catch (MetricTypeMismatchException e) {
                        e.printStackTrace();
                    }
                }

                String oprText = ((EditText) findViewById(R.id.oprTextBox)).getText().toString();
                float opr = -1;
                if (!oprText.equals("") && !oprText.equals(" "))
                    opr = Float.parseFloat(oprText);
                /*new Robot(Integer.parseInt(getIntent().getStringExtra(TEAM_NUMBER_EXTRA)),((Spinner) findViewById(R.id.gameSpinner)).getSelectedItem().toString(),
                        ((EditText) findViewById(R.id.comments)).getText().toString(),
                        opr,
                        vals);*/
                setResult(RESULT_OK);
                finish();

                break;

            case R.id.cancel:

                finish();

                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapter, View v, int selectedItem, long id) {

        /*LinearLayout metricList = (LinearLayout) findViewById(R.id.metricList);
        metricList.removeAllViews();

        Metric[] metrics = new Select().from(Metric.class).where("Game = ?", )

        for (int i = 0; i < metrics.length; i++) {

            MetricWidget m = MetricWidget.createWidget(this, metrics[i]);
            m.setTag(metrics[i].getKey());
            metricList.addView(m);
        }*/
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapter) {
        ((LinearLayout) findViewById(R.id.metricList)).removeAllViews();
    }
}
