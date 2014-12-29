package com.team2052.frckrawler.view.metric;

import android.content.Context;
import android.os.Parcelable;
import android.widget.SeekBar;
import android.widget.TextView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.database.MetricValue;
import com.team2052.frckrawler.database.serializers.StringArrayDeserializer;

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

        String[] range = StringArrayDeserializer.deserialize(m.getMetric().getRange());
        if (range.length > 0)
            min = Integer.parseInt(range[0]);

        if (range.length > 1) {
            max = Integer.parseInt(range[1]);
            s.setMax(max - min);
        }

        if (m.getValue() != null && !m.getValue().equals(""))
            value = Integer.parseInt(m.getValue());
        else
            value = min;

        if (value < min || value > max)
            value = min;

        s.setProgress(value - min);
        s.setOnSeekBarChangeListener(this);

        ((TextView) findViewById(R.id.min)).setText(Integer.toString(min));
        ((TextView) findViewById(R.id.max)).setText(Integer.toString(max));
        ((TextView) findViewById(R.id.value)).setText(Integer.toString(value));
    }

    @Override
    public String getValues() {
        return Integer.toString(value);
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
}
