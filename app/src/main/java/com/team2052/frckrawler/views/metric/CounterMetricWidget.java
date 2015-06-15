package com.team2052.frckrawler.views.metric;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.database.MetricValue;
import com.team2052.frckrawler.tba.JSON;


public class CounterMetricWidget extends MetricWidget implements OnClickListener {

    int value;
    private int max;
    private int min;
    private int increment;

    public CounterMetricWidget(Context context, MetricValue m) {

        super(context, m);
        inflater.inflate(R.layout.widget_metric_counter, this);

        ((TextView) findViewById(R.id.title)).setText(m.getMetric().getName());

        findViewById(R.id.plus).setOnClickListener(this);
        findViewById(R.id.minus).setOnClickListener(this);

        JsonObject o = JSON.getAsJsonObject(m.getMetric().getData());

        max = o.get("max").getAsInt();
        min = o.get("min").getAsInt();
        increment = o.get("inc").getAsInt();

        if (m.getValue() != null)
            value = JSON.getAsJsonObject(m.getValue()).get("value").getAsInt();
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
