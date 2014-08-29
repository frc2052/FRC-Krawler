package com.team2052.frckrawler.activity.dialog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activity.BaseActivity;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.models.Metric;
import com.team2052.frckrawler.gui.ListEditor;
import com.team2052.frckrawler.gui.MathMetricListEditor;
import com.team2052.frckrawler.gui.TextListEditor;

import java.util.List;

public class EditMetricDialogActivity extends BaseActivity implements OnClickListener {

    public static final String METRIC_ID = "METRIC_ID";

    private int metricCategory;
    private Metric metric;
    private ListEditor list;

    public static Intent newInstance(Context c, Metric metric) {
        Intent i = new Intent(c, EditMetricDialogActivity.class);
        i.putExtra(METRIC_ID, metric.getId());
        return i;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialogactivity_edit_metric);
        findViewById(R.id.cancel).setOnClickListener(this);
        findViewById(R.id.remove).setOnClickListener(this);
        findViewById(R.id.saveMetric).setOnClickListener(this);

        metric = Metric.load(Metric.class, getIntent().getLongExtra(METRIC_ID, -1));

        if (metric != null) {
            String[] metricTypes = getResources().getStringArray(R.array.metric_types);
            ((TextView) findViewById(R.id.type)).setText(metricTypes[metric.type]);
            ((EditText) findViewById(R.id.name)).setText(metric.name);
            ((EditText) findViewById(R.id.description)).setText(metric.description);
            ((CheckBox) findViewById(R.id.displayed)).setChecked(metric.display);
            Object[] range = metric.parseRange();

            switch (metric.type) {
                case Metric.CHOOSER:
                    list = new TextListEditor(this);
                    for (int i = 0; i < range.length; i++)
                        list.addValue(range[i].toString(), range[i].toString());
                    ((FrameLayout) findViewById(R.id.listEditorSlot)).addView(list);

                case Metric.TEXT:
                case Metric.BOOLEAN:
                    findViewById(R.id.min).setEnabled(false);
                    findViewById(R.id.max).setEnabled(false);
                    findViewById(R.id.inc).setEnabled(false);
                    break;

                case Metric.COUNTER:

                    ((EditText) findViewById(R.id.min)).setText((String) range[0]);
                    ((EditText) findViewById(R.id.max)).setText((String) range[1]);
                    ((EditText) findViewById(R.id.inc)).setText((String) range[2]);

                    break;

                case Metric.SLIDER:

                    ((EditText) findViewById(R.id.min)).setText((String) range[0]);
                    ((EditText) findViewById(R.id.max)).setText((String) range[1]);
                    findViewById(R.id.inc).setEnabled(false);

                    break;

                case Metric.MATH:
                    findViewById(R.id.min).setEnabled(false);
                    findViewById(R.id.max).setEnabled(false);
                    findViewById(R.id.inc).setEnabled(false);

                    List<Metric> addableMetrics = new Select().from(Metric.class).where("Game = ?", metric.game.getId()).where("Category = ?", metric.category).where("Type = ?", Metric.COUNTER).where("Type = ?", Metric.SLIDER).execute();
                    String[] metricNames = new String[range.length];

                    for (int i = 0; i < range.length; i++) {
                        for (Metric met : addableMetrics) {
                            if (met.getId() == Integer.valueOf((String) range[i]).intValue()) {
                                metricNames[i] = met.name;
                                break;
                            }
                        }
                    }

                    list = new MathMetricListEditor(this, new String[0], (Metric[]) addableMetrics.toArray());

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
                metric.delete();
                finish();
                break;

            case R.id.saveMetric:
                Object[] range = null;
                switch (metric.type) {
                    case Metric.TEXT:
                    case Metric.BOOLEAN:
                        break;
                    case Metric.COUNTER:
                        range = new Object[3];
                        range[0] = Integer.valueOf(((EditText) findViewById(R.id.min)).getText().toString());
                        range[1] = Integer.valueOf(((EditText) findViewById(R.id.max)).getText().toString());
                        range[2] = Integer.valueOf(((EditText) findViewById(R.id.inc)).getText().toString());
                        break;
                    case Metric.SLIDER:
                        range = new Object[2];
                        range[0] = Integer.valueOf(((EditText) findViewById(R.id.min)).getText().toString());
                        range[1] = Integer.valueOf(((EditText) findViewById(R.id.max)).getText().toString());
                        break;
                    case Metric.MATH:
                    case Metric.CHOOSER:
                        range = list.getValues();
                        break;
                }

                //Save the metric
                metric.name = ((EditText) findViewById(R.id.name)).getText().toString();
                metric.description = ((EditText) findViewById(R.id.description)).getText().toString();
                metric.display = ((CheckBox) findViewById(R.id.displayed)).isChecked();
                if (range != null) {
                    metric.range = Metric.unparseRange(range);
                }
                metric.save();

                finish();
        }
    }
}
