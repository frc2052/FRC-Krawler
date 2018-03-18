package com.team2052.frckrawler.adapters.items.smart.metric;

import android.content.Context;

import com.team2052.frckrawler.metric.data.CompiledMetricValue;

import io.nlopez.smartadapters.views.BindableFrameLayout;

public class IndexValueSummaryCard extends BindableFrameLayout<CompiledMetricValue> {
    public IndexValueSummaryCard(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return 0;
    }

    @Override
    public void bind(CompiledMetricValue compiledMetricValue) {

    }
}
