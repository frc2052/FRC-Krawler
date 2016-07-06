package com.team2052.frckrawler.views.metric.impl;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.database.metric.MetricValue;
import com.team2052.frckrawler.tba.JSON;
import com.team2052.frckrawler.views.metric.MetricWidget;


public class CounterMetricWidget extends MetricWidget implements OnClickListener {

    int value;
    private int max;
    private int min;
    private int increment;

    public CounterMetricWidget(Context context, MetricValue metricValue) {
        super(context, metricValue);
        inflater.inflate(R.layout.widget_metric_counter, this);
        setMetricValue(metricValue);
    }

    public CounterMetricWidget(Context context) {
        super(context);
        inflater.inflate(R.layout.widget_metric_counter, this);
    }

    @Override
    public void setMetricValue(MetricValue m) {
        ((TextView) findViewById(R.id.title)).setText(m.getMetric().getName());

        findViewById(R.id.plus).setOnClickListener(this);
        findViewById(R.id.minus).setOnClickListener(this);

        JsonObject o = JSON.getAsJsonObject(m.getMetric().getData());

        max = o.get("max").getAsInt();
        min = o.get("min").getAsInt();
        increment = o.get("inc").getAsInt();

        if (m.getValue() != null)
            value = m.getValue().getAsJsonObject().get("value").getAsInt();
        else
            value = min;

        ((TextView) findViewById(R.id.value)).setText(Integer.toString(value));
    }


    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.plus) {

            value += increment;

            if (value > max)
                value = max;

        } else if (v.getId() == R.id.minus) {

            value -= increment;

            if (value < min)
                value = min;
        }

        ((TextView) findViewById(R.id.value)).setText(Integer.toString(value));
    }

    @Override
    public JsonElement getData() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("value", value);
        return jsonObject;
    }
}
