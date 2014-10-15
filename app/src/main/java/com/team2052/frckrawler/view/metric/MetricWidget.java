package com.team2052.frckrawler.view.metric;

import android.content.Context;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.team2052.frckrawler.database.MetricValue;
import com.team2052.frckrawler.database.MetricValues;

import frckrawler.Metric;
import icepick.Icepick;

public abstract class MetricWidget extends FrameLayout
{

    protected LayoutInflater inflater;
    private Metric metric;

    protected MetricWidget(Context context, Metric m, String val)
    {
        super(context);
        metric = m;
        inflater = (LayoutInflater) context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
    }

    public static MetricWidget createWidget(Context c, Metric m)
    {
        return createWidget(c, new MetricValue(m, ""));
    }

    public static MetricWidget createWidget(Context c, MetricValue m)
    {
        if (m == null)
            return null;

        switch (m.getMetric().getType()) {
            case MetricValues.BOOLEAN:
                return new BooleanMetricWidget(c, m);
            case MetricValues.CHOOSER:
                return new ChooserMetricWidget(c, m);
            case MetricValues.COUNTER:
                return new CounterMetricWidget(c, m);
            case MetricValues.SLIDER:
                return new SliderMetricWidget(c, m);
            default:
                return null;
        }
    }

    public Metric getMetric()
    {
        return metric;
    }

    public MetricValue getMetricValue()
    {
        return new MetricValue(metric, getValues());
    }

    public abstract String getValues();

}
