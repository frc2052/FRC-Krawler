package com.team2052.frckrawler.view.metric;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.team2052.frckrawler.database.MetricValue;
import com.team2052.frckrawler.database.MetricValue.MetricTypeMismatchException;
import com.team2052.frckrawler.database.MetricValues;

import frckrawler.Metric;

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
        try {
            return createWidget(c, new MetricValue(m, ""));
        } catch (MetricTypeMismatchException e) {
            return null;
        }
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
        try {
            return new MetricValue(metric, getValues());
        } catch (MetricTypeMismatchException e) {
            e.printStackTrace();
            return null;
        }
    }

    public abstract String getValues();
}
