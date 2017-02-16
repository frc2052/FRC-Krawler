package com.team2052.frckrawler.metrics.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.google.common.base.Optional;
import com.google.gson.JsonElement;
import com.team2052.frckrawler.database.metric.MetricValue;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.metric.MetricTypeEntryHandler;

public abstract class MetricWidget extends FrameLayout {

    protected LayoutInflater inflater;
    private Metric metric;

    protected MetricWidget(Context context, MetricValue m) {
        this(context);
        metric = m.getMetric();
    }

    protected MetricWidget(Context context) {
        super(context);
        inflater = (LayoutInflater) context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        initViews();
    }

    public static Optional<MetricWidget> createWidget(Context c, Metric m) {
        return createWidget(c, new MetricValue(m, null));
    }

    public static Optional<MetricWidget> createWidget(Context c, MetricValue m) {
        if (m == null)
            return Optional.absent();
        return Optional.of(MetricTypeEntryHandler.INSTANCE.getTypeEntry(m.getMetric().getType()).getWidget(c, m));
    }

    public abstract void setMetricValue(MetricValue m);

    public abstract void initViews();

    public MetricValue getValue() {
        return new MetricValue(getMetric(), getData());
    }

    public Metric getMetric() {
        return metric;
    }

    public abstract JsonElement getData();
}
