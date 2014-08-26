package com.team2052.frckrawler.activity.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activity.MetricsActivity;
import com.team2052.frckrawler.database.DBContract;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.Metric;
import com.team2052.frckrawler.gui.ListEditor;
import com.team2052.frckrawler.gui.MathMetricListEditor;
import com.team2052.frckrawler.gui.TextListEditor;

public class EditMetricDialogActivity extends Activity implements OnClickListener {

    public static final String METRIC_ID_EXTRA = "com.team2052.frckrawler.metricIDExtra";
    public static final String METRIC_CATEGORY_EXTRA = "com.team2052.frckrawler.categoryExtra";

    private int metricCategory;
    private Metric metric;
    private DBManager db;
    private ListEditor list;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialogactivity_edit_metric);
        findViewById(R.id.cancel).setOnClickListener(this);
        findViewById(R.id.remove).setOnClickListener(this);
        findViewById(R.id.saveMetric).setOnClickListener(this);

        metricCategory = getIntent().getIntExtra(METRIC_CATEGORY_EXTRA, 1);

        db = DBManager.getInstance(this);

        switch (metricCategory) {
            case MetricsActivity.MATCH_PERF_METRICS:
                Metric[] arr = db.getMatchPerformanceMetricsByColumns
                        (new String[]{DBContract.COL_METRIC_ID},
                                new String[]{Integer.toString
                                        (getIntent().getIntExtra(METRIC_ID_EXTRA, -1))}
                        );
                if (arr.length > 0)
                    metric = arr[0];
                break;

            case MetricsActivity.ROBOT_METRICS:
                Metric[] rarr = db.getRobotMetricsByColumns
                        (new String[]{DBContract.COL_METRIC_ID},
                                new String[]{Integer.toString
                                        (getIntent().getIntExtra(METRIC_ID_EXTRA, -1))}
                        );
                if (rarr.length > 0)
                    metric = rarr[0];
                break;
        }

        if (metric != null) {
            String[] metricTypes = getResources().getStringArray(R.array.metric_types);
            ((TextView) findViewById(R.id.type)).setText(metricTypes[metric.getType()]);
            ((EditText) findViewById(R.id.name)).setText(metric.getMetricName());
            ((EditText) findViewById(R.id.description)).setText(metric.getDescription());
            ((CheckBox) findViewById(R.id.displayed)).setChecked(metric.isDisplayed());
            Object[] range = metric.getRange();

            switch (metric.getType()) {
                case DBContract.CHOOSER:
                    list = new TextListEditor(this);
                    for (int i = 0; i < range.length; i++)
                        list.addValue(range[i].toString(), range[i].toString());
                    ((FrameLayout) findViewById(R.id.listEditorSlot)).addView(list);

                case DBContract.TEXT:
                case DBContract.BOOLEAN:
                    ((EditText) findViewById(R.id.min)).setEnabled(false);
                    ((EditText) findViewById(R.id.max)).setEnabled(false);
                    ((EditText) findViewById(R.id.inc)).setEnabled(false);
                    break;

                case DBContract.COUNTER:

                    ((EditText) findViewById(R.id.min)).setText((String) range[0]);
                    ((EditText) findViewById(R.id.max)).setText((String) range[1]);
                    ((EditText) findViewById(R.id.inc)).setText((String) range[2]);

                    break;

                case DBContract.SLIDER:

                    ((EditText) findViewById(R.id.min)).setText((String) range[0]);
                    ((EditText) findViewById(R.id.max)).setText((String) range[1]);
                    ((EditText) findViewById(R.id.inc)).setEnabled(false);

                    break;

                case DBContract.MATH:
                    ((EditText) findViewById(R.id.min)).setEnabled(false);
                    ((EditText) findViewById(R.id.max)).setEnabled(false);
                    ((EditText) findViewById(R.id.inc)).setEnabled(false);

                    Metric[] addableMetrics = new Metric[0];
                    DBManager db = DBManager.getInstance(this);

                    if (metricCategory == MetricsActivity.MATCH_PERF_METRICS) {
                        addableMetrics = db.getMatchPerformanceMetricsByColumns(
                                new String[]{DBContract.COL_TYPE, DBContract.COL_TYPE},
                                new String[]{Integer.toString(DBContract.COUNTER),
                                        Integer.toString(DBContract.SLIDER)},
                                true
                        );

                    } else if (metricCategory == MetricsActivity.ROBOT_METRICS) {
                        addableMetrics = db.getRobotMetricsByColumns(
                                new String[]{DBContract.COL_TYPE, DBContract.COL_TYPE},
                                new String[]{Integer.toString(DBContract.COUNTER),
                                        Integer.toString(DBContract.SLIDER)},
                                true
                        );
                    }

                    String[] metricNames = new String[range.length];

                    for (int i = 0; i < range.length; i++) {
                        for (int k = 0; k < addableMetrics.length; k++) {
                            if (addableMetrics[k].getID() ==
                                    Integer.valueOf((String) range[i]).intValue() &&
                                    addableMetrics[k].getGameName().
                                            equals(metric.getGameName())) {
                                metricNames[i] = addableMetrics[k].getMetricName();
                                break;
                            }
                        }
                    }

                    list = new MathMetricListEditor(this, new String[0], addableMetrics);

                    for (int i = 0; i < range.length; i++)
                        list.addValue(range[i].toString(), metricNames[i]);
                    ((FrameLayout) findViewById(R.id.listEditorSlot)).addView(list);
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        DBManager db = DBManager.getInstance(this);
        switch (v.getId()) {
            case R.id.cancel:
                finish();
                break;

            case R.id.remove:
                switch (metricCategory) {
                    case MetricsActivity.MATCH_PERF_METRICS:
                        db.removeMatchPerformaceMetric
                                (getIntent().getIntExtra(METRIC_ID_EXTRA, -1));
                        break;

                    case MetricsActivity.ROBOT_METRICS:
                        db.removeRobotMetric(getIntent().getIntExtra
                                (METRIC_ID_EXTRA, -1));
                        break;

                    case MetricsActivity.DRIVER_METRICS:
                        db.removeDriverMetric(getIntent().getIntExtra
                                (METRIC_ID_EXTRA, -1));
                        break;
                }

                finish();
                break;

            case R.id.saveMetric:
                Object[] range = null;
                switch (metric.getType()) {
                    case DBContract.TEXT:
                    case DBContract.BOOLEAN:
                        break;

                    case DBContract.COUNTER:
                        range = new Object[3];
                        range[0] = Integer.valueOf(((EditText) findViewById(R.id.min)).
                                getText().toString());
                        range[1] = Integer.valueOf(((EditText) findViewById(R.id.max)).
                                getText().toString());
                        range[2] = Integer.valueOf(((EditText) findViewById(R.id.inc)).
                                getText().toString());
                        break;

                    case DBContract.SLIDER:

                        range = new Object[2];
                        range[0] = Integer.valueOf(((EditText) findViewById(R.id.min)).
                                getText().toString());
                        range[1] = Integer.valueOf(((EditText) findViewById(R.id.max)).
                                getText().toString());

                        break;

                    case DBContract.MATH:
                    case DBContract.CHOOSER:
                        range = list.getValues();
                        break;
                }

                switch (metricCategory) {
                    case MetricsActivity.MATCH_PERF_METRICS:
                        db.updateMatchPerformanceMetrics(
                                new Metric[]{new Metric(
                                        metric.getID(),
                                        metric.getGameName(),
                                        ((EditText) findViewById(R.id.name)).getText().toString(),
                                        ((EditText) findViewById(R.id.description)).getText().toString(),
                                        metric.getKey(),
                                        metric.getType(),
                                        range,
                                        ((CheckBox) findViewById(R.id.displayed)).isChecked()
                                )}
                        );

                        break;

                    case MetricsActivity.ROBOT_METRICS:
                        db.updateRobotMetrics(
                                new Metric[]{new Metric(
                                        metric.getID(),
                                        metric.getGameName(),
                                        ((EditText) findViewById(R.id.name)).getText().toString(),
                                        ((EditText) findViewById(R.id.description)).getText().toString(),
                                        metric.getKey(),
                                        metric.getType(),
                                        range,
                                        ((CheckBox) findViewById(R.id.displayed)).isChecked()
                                )}
                        );

                        break;

                    case MetricsActivity.DRIVER_METRICS:
                        //NOT USED!//
                        break;
                }

                finish();

                break;
        }
    }
}
