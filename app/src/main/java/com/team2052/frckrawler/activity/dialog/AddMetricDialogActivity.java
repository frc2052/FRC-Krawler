package com.team2052.frckrawler.activity.dialog;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activity.BaseActivity;
import com.team2052.frckrawler.activity.MetricsActivity;
import com.team2052.frckrawler.database.DBContract;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.Metric;
import com.team2052.frckrawler.gui.ListEditor;
import com.team2052.frckrawler.gui.MathMetricListEditor;
import com.team2052.frckrawler.gui.TextListEditor;

import java.util.ArrayList;

public class AddMetricDialogActivity extends BaseActivity implements OnClickListener, OnItemSelectedListener {

    public static final String GAME_NAME_EXTRA = "com.team2052.frckrawler.gameNameExtra";
    public static final String METRIC_CATEGORY_EXTRA = "com.team2052.frckrawler.categoryExtra";

    private int metricCategory;
    private int selectedMetricType;
    private ListEditor list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialogactivity_add_metric);

        list = new TextListEditor(this);
        ((FrameLayout) findViewById(R.id.listEditorSlot)).addView(list);

        findViewById(R.id.add).setOnClickListener(this);
        findViewById(R.id.cancel).setOnClickListener(this);
        ((Spinner) findViewById(R.id.type)).setOnItemSelectedListener(this);
        ((Spinner) findViewById(R.id.type)).setSelection(0);

        metricCategory = getIntent().getIntExtra(METRIC_CATEGORY_EXTRA, 1);
        selectedMetricType = 0;
        refreshTypeBasedUI();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.add) {
            Metric m = null;
            switch (selectedMetricType) {
                case DBContract.BOOLEAN:
                    m = Metric.MetricFactory.createBooleanMetric(
                            getIntent().getStringExtra(GAME_NAME_EXTRA),
                            ((EditText) findViewById(R.id.name)).getText().toString(),
                            ((EditText) findViewById(R.id.description)).getText().toString(),
                            ((CheckBox) findViewById(R.id.displayed)).isChecked()
                    );
                    break;

                case DBContract.COUNTER:
                    try {
                        m = Metric.MetricFactory.createCounterMetric(
                                getIntent().getStringExtra(GAME_NAME_EXTRA),
                                ((EditText) findViewById(R.id.name)).getText().toString(),
                                ((EditText) findViewById(R.id.description)).getText().toString(),
                                Integer.parseInt(((EditText) findViewById(R.id.min)).getText().toString()),
                                Integer.parseInt(((EditText) findViewById(R.id.max)).getText().toString()),
                                Integer.parseInt(((EditText) findViewById(R.id.inc)).getText().toString()),
                                ((CheckBox) findViewById(R.id.displayed)).isChecked()
                        );
                    } catch (NumberFormatException e) {

                        Toast.makeText(this, "Could not create addbutton. Make sure you " +
                                        "have filled out all of the necessary fields.",
                                Toast.LENGTH_SHORT
                        ).show();
                        return;
                    }
                    break;

                case DBContract.SLIDER:
                    try {
                        m = Metric.MetricFactory.createSliderMetric(
                                getIntent().getStringExtra(GAME_NAME_EXTRA),
                                ((EditText) findViewById(R.id.name)).getText().toString(),
                                ((EditText) findViewById(R.id.description)).getText().toString(),
                                Integer.parseInt(((EditText) findViewById(R.id.min)).getText().toString()),
                                Integer.parseInt(((EditText) findViewById(R.id.max)).getText().toString()),
                                ((CheckBox) findViewById(R.id.displayed)).isChecked()
                        );
                    } catch (NumberFormatException e) {

                        Toast.makeText(this, "Could not create addbutton. Make sure you " +
                                        "have filled out all of the necessary fields.",
                                Toast.LENGTH_SHORT
                        ).show();
                        return;
                    }
                    break;

                case DBContract.CHOOSER:
                    m = Metric.MetricFactory.createChooserMetric(
                            getIntent().getStringExtra(GAME_NAME_EXTRA),
                            ((EditText) findViewById(R.id.name)).getText().toString(),
                            ((EditText) findViewById(R.id.description)).getText().toString(),
                            list.getValues(),
                            ((CheckBox) findViewById(R.id.displayed)).isChecked()
                    );
                    break;

                case DBContract.TEXT:
                    m = Metric.MetricFactory.createTextMetric(
                            getIntent().getStringExtra(GAME_NAME_EXTRA),
                            ((EditText) findViewById(R.id.name)).getText().toString(),
                            ((EditText) findViewById(R.id.description)).getText().toString(),
                            ((CheckBox) findViewById(R.id.displayed)).isChecked()
                    );
                    break;

                case DBContract.MATH:
                    String[] selectedMetrics = list.getValues();
                    Integer[] selectedMetricIDs = new Integer[selectedMetrics.length];

                    for (int i = 0; i < selectedMetrics.length; i++)
                        selectedMetricIDs[i] = Integer.valueOf(selectedMetrics[i]);

                    m = Metric.MetricFactory.createMathMetric(
                            getIntent().getStringExtra(GAME_NAME_EXTRA),
                            ((EditText) findViewById(R.id.name)).getText().toString(),
                            ((EditText) findViewById(R.id.description)).getText().toString(),
                            selectedMetricIDs,
                            ((CheckBox) findViewById(R.id.displayed)).isChecked()
                    );

                    break;
            }

            DBManager db = DBManager.getInstance(this);

            switch (metricCategory) {
                case MetricsActivity.MATCH_PERF_METRICS:
                    db.addMatchPerformanceMetric(m);
                    break;

                case MetricsActivity.ROBOT_METRICS:
                    db.addRobotMetric(m);
                    break;

                case MetricsActivity.DRIVER_METRICS:
                    db.addDriverMetric(m);
                    break;
            }

            finish();

        } else if (v.getId() == R.id.cancel) {
            finish();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int pos,
                               long id) {
        selectedMetricType = pos;
        refreshTypeBasedUI();
    }

    @Override
    public void onNothingSelected(AdapterView<?> v) {
        ((Spinner) findViewById(R.id.type)).setSelection(0);
        refreshTypeBasedUI();
    }

    protected void refreshTypeBasedUI() {
        if (selectedMetricType == DBContract.COUNTER) {
            findViewById(R.id.min).setEnabled(true);
            findViewById(R.id.max).setEnabled(true);
            findViewById(R.id.inc).setEnabled(true);
            ((FrameLayout) findViewById(R.id.listEditorSlot)).removeAllViews();

        } else if (selectedMetricType == DBContract.SLIDER) {
            findViewById(R.id.min).setEnabled(true);
            findViewById(R.id.max).setEnabled(true);
            findViewById(R.id.inc).setEnabled(false);
            ((FrameLayout) findViewById(R.id.listEditorSlot)).removeAllViews();

        } else if (selectedMetricType == DBContract.MATH) {
            DBManager db = DBManager.getInstance(this);
            Metric[] choices;

            switch (metricCategory) {
                case MetricsActivity.MATCH_PERF_METRICS:
                    Metric[] matchMetrics = db.getMatchPerformanceMetricsByColumns
                            (new String[]{DBContract.COL_GAME_NAME},
                                    new String[]{getIntent().getStringExtra
                                            (GAME_NAME_EXTRA)}
                            );
                    ArrayList<Metric> acceptedMetrics = new ArrayList<Metric>();
                    for (Metric met : matchMetrics) {
                        if (met.getType() == DBContract.COUNTER ||
                                met.getType() == DBContract.SLIDER)
                            acceptedMetrics.add(met);
                    }

                    choices = acceptedMetrics.toArray(new Metric[0]);
                    break;

                case MetricsActivity.ROBOT_METRICS:
                    Metric[] robotMetrics = db.getRobotMetricsByColumns
                            (new String[]{DBContract.COL_GAME_NAME},
                                    new String[]{getIntent().getStringExtra(GAME_NAME_EXTRA)});
                    ArrayList<Metric> choosableMetrics = new ArrayList<Metric>();
                    for (Metric m : robotMetrics)
                        if (m.getType() == DBContract.COUNTER ||
                                m.getType() == DBContract.SLIDER)
                            choosableMetrics.add(m);
                    choices = choosableMetrics.toArray(new Metric[0]);
                    break;

                default:
                    choices = new Metric[0];
            }

            findViewById(R.id.min).setEnabled(false);
            findViewById(R.id.max).setEnabled(false);
            findViewById(R.id.inc).setEnabled(false);
            list = new MathMetricListEditor(this, new String[0], choices);
            ((FrameLayout) findViewById(R.id.listEditorSlot)).removeAllViews();
            ((FrameLayout) findViewById(R.id.listEditorSlot)).addView(list);

        } else if (selectedMetricType == DBContract.CHOOSER) {
            findViewById(R.id.min).setEnabled(false);
            findViewById(R.id.max).setEnabled(false);
            findViewById(R.id.inc).setEnabled(false);
            list = new TextListEditor(this);
            ((FrameLayout) findViewById(R.id.listEditorSlot)).removeAllViews();
            ((FrameLayout) findViewById(R.id.listEditorSlot)).addView(list);

        } else {
            findViewById(R.id.min).setEnabled(false);
            findViewById(R.id.max).setEnabled(false);
            findViewById(R.id.inc).setEnabled(false);
            ((FrameLayout) findViewById(R.id.listEditorSlot)).removeAllViews();
        }
    }
}
