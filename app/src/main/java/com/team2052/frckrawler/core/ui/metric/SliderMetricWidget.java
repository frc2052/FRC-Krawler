package com.team2052.frckrawler.core.ui.metric;

import android.content.Context;
import android.os.Parcelable;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.core.database.MetricValue;
import com.team2052.frckrawler.core.tba.JSON;

public class SliderMetricWidget extends MetricWidget implements SeekBar.OnSeekBarChangeListener {

    int value;
    private int min;
    private int max;

    public SliderMetricWidget(Context context, MetricValue m) {

        super(context, m.getMetric(), m.getValue());
        inflater.inflate(R.layout.widget_metric_slider, this);

        ((TextView) findViewById(R.id.name)).setText(m.getMetric().getName());

        min = 0;
        max = 1;

        SeekBar s = (SeekBar) findViewById(R.id.sliderVal);

        JsonObject range = JSON.getAsJsonObject(m.getMetric().getRange());
        min = range.get("min").getAsInt();
        max = range.get("max").getAsInt();

        if (m.getValue() != null && !m.getValue().equals(""))
            value = JSON.getAsJsonObject(m.getValue()).get("value").getAsInt();
        else
            value = min;

        if (value < min || value > max)
            value = min;

        s.setMax(max - min);
        s.setProgress(value - min);
        s.setOnSeekBarChangeListener(this);

        ((TextView) findViewById(R.id.min)).setText(Integer.toString(min));
        ((TextView) findViewById(R.id.max)).setText(Integer.toString(max));
        ((TextView) findViewById(R.id.value)).setText(Integer.toString(value));
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        value = seekBar.getProgress() + min;
        ((TextView) findViewById(R.id.value)).setText(Integer.toString(value));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        value = seekBar.getProgress() + min;
        ((TextView) findViewById(R.id.value)).setText(Integer.toString(value));
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        value = seekBar.getProgress() + min;
        ((TextView) findViewById(R.id.value)).setText(Integer.toString(value));
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof MetricWidgetSavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        MetricWidgetSavedState ss = (MetricWidgetSavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        value = Integer.parseInt(ss.value);
    }

    @Override
    public MetricValue getMetricValue() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("value", value);
        return new MetricValue(getMetric(), JSON.getGson().toJson(jsonObject));
    }
}
