package com.team2052.frckrawler.metric.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.google.gson.JsonElement;
import com.team2052.frckrawler.metric.data.MetricValue;
import com.team2052.frckrawler.models.Metric;

public abstract class MetricWidget extends FrameLayout {

    protected LayoutInflater inflater;
    private Metric metric;

    protected MetricWidget(Context context, MetricValue m) {
        this(context);
        metric = m.metric();
        setMetricValue(m);
    }

    protected MetricWidget(Context context) {
        super(context);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        initViews();
    }

    public abstract void setMetricValue(MetricValue m);

    public abstract void initViews();

    public final MetricValue getValue() {
        return MetricValue.create(metric, getData());
    }

    public Metric getMetric() {
        return metric;
    }

    public abstract JsonElement getData();
}
