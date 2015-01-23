package com.team2052.frckrawler.core.ui.metric;

import android.content.Context;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.google.gson.JsonElement;
import com.team2052.frckrawler.core.database.MetricValue;
import com.team2052.frckrawler.core.tba.JSON;
import com.team2052.frckrawler.core.util.Utilities;
import com.team2052.frckrawler.db.Metric;

public abstract class MetricWidget extends FrameLayout implements IMetricWidget{

    protected LayoutInflater inflater;
    private Metric metric;

    protected MetricWidget(Context context, Metric m, String val) {
        super(context);
        metric = m;
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
            case Utilities.MetricUtil.BOOLEAN:
                return new BooleanMetricWidget(c, m);
            case Utilities.MetricUtil.CHOOSER:
                return new ChooserMetricWidget(c, m);
            case Utilities.MetricUtil.COUNTER:
                return new CounterMetricWidget(c, m);
            case Utilities.MetricUtil.SLIDER:
                return new SliderMetricWidget(c, m);
            default:
                return null;
        }
    }

    public Metric getMetric() {
        return metric;
    }

    public MetricValue getValue() {
        return new MetricValue(getMetric(), JSON.getGson().toJson(getData()));
    }
}
