package com.team2052.frckrawler.activities;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.databinding.ActivityMetricBinding;
import com.team2052.frckrawler.db.Metric;

/**
 * Created by Adam on 6/13/2015.
 */
public class MetricActivity extends BaseActivity {
    public static final String METRIC_ID = "METRIC_ID";

    public static Intent newInstance(Context context, Metric metric) {
        Intent intent = new Intent(context, MetricActivity.class);
        intent.putExtra(METRIC_ID, metric.getId());
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMetricBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_metric);
        binding.setNumOfData(10);
    }
}
