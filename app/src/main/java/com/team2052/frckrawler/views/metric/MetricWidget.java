package com.team2052.frckrawler.views.metric;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.google.common.base.Optional;
import com.google.gson.JsonElement;
import com.team2052.frckrawler.database.metric.MetricHelper;
import com.team2052.frckrawler.database.metric.MetricValue;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.views.metric.impl.BooleanMetricWidget;
import com.team2052.frckrawler.views.metric.impl.CheckBoxMetricWidget;
import com.team2052.frckrawler.views.metric.impl.ChooserMetricWidget;
import com.team2052.frckrawler.views.metric.impl.CounterMetricWidget;
import com.team2052.frckrawler.views.metric.impl.SliderMetricWidget;

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
    }

    public static Optional<MetricWidget> createWidget(Context c, Metric m) {
        return createWidget(c, new MetricValue(m, null));
    }

    public static Optional<MetricWidget> createWidget(Context c, MetricValue m) {
        if (m == null)
            return Optional.absent();

        switch (m.getMetric().getType()) {
            case MetricHelper.BOOLEAN:
                return Optional.of(new BooleanMetricWidget(c, m));
            case MetricHelper.CHOOSER:
                return Optional.of(new ChooserMetricWidget(c, m));
            case MetricHelper.COUNTER:
                return Optional.of(new CounterMetricWidget(c, m));
            case MetricHelper.SLIDER:
                return Optional.of(new SliderMetricWidget(c, m));
            case MetricHelper.CHECK_BOX:
                return Optional.of(new CheckBoxMetricWidget(c, m));
            default:
                return Optional.absent();
        }
    }

    public abstract void setMetricValue(MetricValue m);

    public MetricValue getValue() {
        return new MetricValue(getMetric(), getData());
    }

    public Metric getMetric() {
        return metric;
    }

    public abstract JsonElement getData();
}
