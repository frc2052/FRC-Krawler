package com.team2052.frckrawler.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.database.MetricHelper;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.fragments.dialog.EditMetricDialogFragment;
import com.team2052.frckrawler.tba.JSON;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Adam on 6/13/2015.
 */
public class MetricActivity extends DatabaseActivity implements CompoundButton.OnCheckedChangeListener {
    public static final String METRIC_ID = "METRIC_ID";
    TextView mName, mDescription, mRangeMin, mRangeMax, mRangeInc, mType;
    SwitchCompat mEnabled;
    private Metric metric;
    private View mMetricRangeCard;

    public static Intent newInstance(Context context, Metric metric) {
        Intent intent = new Intent(context, MetricActivity.class);
        intent.putExtra(METRIC_ID, metric.getId());
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metric);
        mDescription = (TextView) findViewById(R.id.metric_info_description);
        mName = (TextView) findViewById(R.id.metric_info_name);
        mType = (TextView) findViewById(R.id.metric_info_type);
        mEnabled = (SwitchCompat) findViewById(R.id.metric_info_enabled);
        mMetricRangeCard = findViewById(R.id.metric_range_card);
        mRangeInc = (TextView) findViewById(R.id.metric_info_range_inc);
        mRangeMax = (TextView) findViewById(R.id.metric_info_range_max);
        mRangeMin = (TextView) findViewById(R.id.metric_info_range_min);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public void inject() {
        getComponent().inject(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        metric = dbManager.getMetricsTable().load(getIntent().getExtras().getLong(METRIC_ID));
        mEnabled.setChecked(metric.getEnabled());
        mEnabled.setOnCheckedChangeListener(this);
        JsonObject data = JSON.getAsJsonObject(metric.getData());

        if (!Strings.isNullOrEmpty(data.get("description").getAsString())) {
            mDescription.setText(String.format("Description: %s", data.get("description").getAsString()));
        } else {
            mDescription.setText("No Description Provided");
        }

        mName.setText(String.format("Name: %s", metric.getName()));
        switch (metric.getType()) {
            case MetricHelper.BOOLEAN:
                mType.setText("Boolean");
                break;
            case MetricHelper.COUNTER:
                mMetricRangeCard.setVisibility(View.VISIBLE);
                mType.setText("Counter");
                mRangeMin.setText(String.format("Min: %s", data.get("min").getAsString()));
                mRangeMax.setText(String.format("Max: %s", data.get("max").getAsString()));
                mRangeInc.setText(String.format("Incrementation: %s", data.get("inc").getAsString()));
                break;
            case MetricHelper.CHECK_BOX:
            case MetricHelper.CHOOSER:
                StringBuilder sb = new StringBuilder();
                String comma = "";
                for (JsonElement value : data.get("values").getAsJsonArray()) {
                    sb.append(comma).append(value.getAsString());
                    comma = ", ";
                }
                mMetricRangeCard.setVisibility(View.VISIBLE);
                mRangeInc.setVisibility(View.GONE);
                mRangeMin.setText(sb.toString());
                mType.setText("Chooser");
                break;
            case MetricHelper.SLIDER:
                mMetricRangeCard.setVisibility(View.VISIBLE);
                mRangeMin.setText(String.format("Min: %s", data.get("min").getAsString()));
                mRangeMax.setText(String.format("Max: %s", data.get("max").getAsString()));
                mRangeInc.setVisibility(View.GONE);
                mType.setText("Slider");
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_delete_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_edit) {
            EditMetricDialogFragment.newInstance(metric).show(getSupportFragmentManager(), "editMetric");
        } else if (item.getItemId() == R.id.menu_delete) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Delete Metric?");
            builder.setMessage("Are you sure you want to delete this metric? You will lose all data associated with this metric.");
            builder.setPositiveButton("Delete", (dialog, which) -> {
                Observable.just(metric)
                        .map(metric -> {
                            dbManager.getMetricsTable().delete(metric);
                            return metric;
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(onNext -> {
                            finish();
                        });
            });
            builder.setNegativeButton("Cancel", null);
            builder.create().show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        metric.setEnabled(isChecked);
        metric.update();
    }
}
