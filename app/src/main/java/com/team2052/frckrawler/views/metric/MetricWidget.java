package com.team2052.frckrawler.views.metric;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.google.gson.JsonElement;
import com.team2052.frckrawler.database.MetricValue;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.tba.JSON;
import com.team2052.frckrawler.util.MetricUtil;

public abstract class MetricWidget extends FrameLayout {

    protected LayoutInflater inflater;
    private Metric metric;

    protected MetricWidget(Context context, MetricValue m) {
        super(context);
        metric = m.getMetric();
        inflater = (LayoutInflater) context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
    }

    public static MetricWidget createWidget(Context c, Metric m) {
        return createWidget(c, new MetricValue(m, null));
    }

    public static MetricWidget createWidget(Context c, MetricValue m) {
        if (m == null)
            return null;

        switch (m.getMetric().getType()) {
            case MetricUtil.BOOLEAN:
                return new BooleanMetricWidget(c, m);
            case MetricUtil.CHOOSER:
                return new ChooserMetricWidget(c, m);
            case MetricUtil.COUNTER:
                return new CounterMetricWidget(c, m);
            case MetricUtil.SLIDER:
                return new SliderMetricWidget(c, m);
            case MetricUtil.CHECK_BOX:
                return new CheckBoxMetricWidget(c, m);
            default:
                return null;
        }
    }

    public MetricValue getValue() {
        return new MetricValue(getMetric(), JSON.getGson().toJson(getData()));
    }

    public Metric getMetric() {
        return metric;
    }

    public abstract JsonElement getData();
}
