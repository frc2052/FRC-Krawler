package com.team2052.frckrawler.gui;

import android.content.Context;
import android.widget.*;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.database.MetricValue;

public class SliderMetricWidget extends MetricWidget implements
        SeekBar.OnSeekBarChangeListener
{

    private int value;
    private int min;
    private int max;

    public SliderMetricWidget(Context context, MetricValue m)
    {

        super(context, m.getMetric(), m.getValue());
        inflater.inflate(R.layout.widget_metric_slider, this);

        ((TextView) findViewById(R.id.name)).setText(m.getMetric().name);

        min = 0;
        max = 1;

        SeekBar s = (SeekBar) findViewById(R.id.sliderVal);

        if (m.getMetric().range.length > 0)
            min = Integer.parseInt((String) m.getMetric().range[0]);

        if (m.getMetric().range.length > 1) {
            max = Integer.parseInt((String) m.getMetric().range[1]);
            s.setMax(max - min);
        }

        if (m.getValue() != null && m.getValue().length > 0)
            value = Integer.parseInt(m.getValue()[0]);
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
    public String[] getValues()
    {

        return new String[]{Integer.toString(value)};
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser)
    {

        value = seekBar.getProgress() + min;
        ((TextView) findViewById(R.id.value)).setText(Integer.toString(value));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar)
    {

        value = seekBar.getProgress() + min;
        ((TextView) findViewById(R.id.value)).setText(Integer.toString(value));
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar)
    {

        value = seekBar.getProgress() + min;
        ((TextView) findViewById(R.id.value)).setText(Integer.toString(value));
    }
}
