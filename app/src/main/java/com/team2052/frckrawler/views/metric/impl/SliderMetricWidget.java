package com.team2052.frckrawler.views.metric.impl;

import android.content.Context;
import android.support.v7.widget.AppCompatSeekBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.database.metric.MetricHelper;
import com.team2052.frckrawler.database.metric.MetricValue;
import com.team2052.frckrawler.tba.JSON;
import com.team2052.frckrawler.views.metric.MetricWidget;

public class SliderMetricWidget extends MetricWidget implements SeekBar.OnSeekBarChangeListener {

    private final AppCompatSeekBar seekBar;
    int value;
    private int min;
    private int max;
    private TextView valueText;

    public SliderMetricWidget(Context context, MetricValue metricValue) {
        super(context, metricValue);
        inflater.inflate(R.layout.widget_metric_slider, this);
        seekBar = (AppCompatSeekBar) findViewById(R.id.sliderVal);
        setMetricValue(metricValue);
    }

    public SliderMetricWidget(Context context) {
        super(context);
        inflater.inflate(R.layout.widget_metric_slider, this);
        seekBar = (AppCompatSeekBar) findViewById(R.id.sliderVal);
    }

    @Override
    public void setMetricValue(MetricValue m) {
        min = 0;
        max = 1;
        ((TextView) findViewById(R.id.name)).setText(m.getMetric().getName());

        JsonObject range = JSON.getAsJsonObject(m.getMetric().getData());
        min = range.get("min").getAsInt();
        max = range.get("max").getAsInt();


        seekBar.setMax(max - min);

        seekBar.setOnSeekBarChangeListener(this);

        ((TextView) findViewById(R.id.min)).setText(Integer.toString(min));
        ((TextView) findViewById(R.id.max)).setText(Integer.toString(max));
        valueText = (TextView) findViewById(R.id.value);

        if (m.getValue() != null && !m.getValue().getAsJsonObject().get("value").isJsonNull())
            value = m.getValue().getAsJsonObject().get("value").getAsInt();
        else
            value = min;

        if (value < min || value > max)
            value = min;
        seekBar.setProgress(value - min);
        valueText.setText(Integer.toString(value));
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        value = seekBar.getProgress() + min;
        valueText.setText(Integer.toString(value));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        value = seekBar.getProgress() + min;
        valueText.setText(Integer.toString(value));
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        value = seekBar.getProgress() + min;
        valueText.setText(Integer.toString(value));
    }

    @Override
    public JsonElement getData() {
        return MetricHelper.buildIntMetricValue(value);
    }
}
