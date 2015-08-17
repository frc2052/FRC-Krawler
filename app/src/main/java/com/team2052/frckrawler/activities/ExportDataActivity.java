package com.team2052.frckrawler.activities;


import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.databinding.ActivityExportDataBinding;

/**
 * Created by Adam on 8/16/2015.
 */
public class ExportDataActivity extends BaseActivity {
    ActivityExportDataBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_export_data);
    }
}
