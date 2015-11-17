package com.team2052.frckrawler.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.common.base.Strings;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.database.MetricHelper;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.fragments.metric.dialog.EditMetricDialogFragment;
import com.team2052.frckrawler.tba.JSON;

/**
 * Created by Adam on 6/13/2015.
 */
public class MetricActivity extends BaseActivity {
    public static final String METRIC_ID = "METRIC_ID";
    private Metric metric;

    public static Intent newInstance(Context context, Metric metric) {
        Intent intent = new Intent(context, MetricActivity.class);
        intent.putExtra(METRIC_ID, metric.getId());
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*binding = DataBindingUtil.setContentView(this, R.layout.activity_metric);
        setSupportActionBar(binding.toolbar);*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*metric = mDbManager.getMetricsTable().load(getIntent().getExtras().getLong(METRIC_ID));

        JsonObject data = JSON.getAsJsonObject(metric.getData());

        if (!Strings.isNullOrEmpty(data.get("description").getAsString())) {
            binding.setDescription(String.format("Description: %s", data.get("description").getAsString()));
        } else {
            binding.setDescription("No Description Provided");
        }
        binding.setName(String.format("Name: %s", metric.getName()));
        switch (MetricHelper.MetricType.values()[metric.getType()]) {
            case BOOLEAN:
                binding.setType("Boolean");
                break;
            case COUNTER:
                binding.metricRangeCard.setVisibility(View.VISIBLE);
                binding.setType("Counter");
                binding.setMin(String.format("Min: %s", data.get("min").getAsString()));
                binding.setMax(String.format("Max: %s", data.get("max").getAsString()));
                binding.setInc(String.format("Incrementation: %s", data.get("inc").getAsString()));
                break;
            case CHECK_BOX:
            case CHOOSER:
                StringBuilder sb = new StringBuilder();
                String comma = "";
                for (JsonElement value : data.get("values").getAsJsonArray()) {
                    sb.append(comma).append(value.getAsString());
                    comma = ", ";
                }
                binding.setType("Chooser");
                break;
            case SLIDER:
                binding.metricRangeCard.setVisibility(View.VISIBLE);
                binding.setMin(String.format("Min: %s", data.get("min").getAsString()));
                binding.setMax(String.format("Max: %s", data.get("max").getAsString()));
                binding.setType("Slider");
                break;
        }*/
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
                mDbManager.getMetricsTable().delete(metric);
                finish();
            });
            builder.setNegativeButton("Cancel", null);
            builder.create().show();
        }
        return super.onOptionsItemSelected(item);
    }
}
