package com.team2052.frckrawler.gui;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.database.MetricValue;

public class CounterMetricWidget extends MetricWidget implements OnClickListener
{

    private int max;
    private int min;
    private int increment;
    private int currentValue;

    public CounterMetricWidget(Context context, MetricValue m)
    {

        super(context, m.getMetric(), m.getValue());
        inflater.inflate(R.layout.widget_metric_counter, this);

        ((TextView) findViewById(R.id.title)).setText(m.getMetric().name);

        findViewById(R.id.plus).setOnClickListener(this);
        findViewById(R.id.minus).setOnClickListener(this);

        Object[] o = m.getMetric().range;

        max = 10;
        min = 0;
        increment = 1;

        if (o.length > 2) {

            min = Integer.parseInt((String) o[0]);
            max = Integer.parseInt((String) o[1]);
            increment = Integer.parseInt((String) o[2]);
        }

        if (m.getValue() != null && m.getValue().length > 0)
            currentValue = Integer.parseInt(m.getValue()[0]);
        else
            currentValue = min;

        ((TextView) findViewById(R.id.value)).setText(Integer.toString(currentValue));
    }

    @Override
    public String[] getValues()
    {
        return new String[]{Integer.toString(currentValue)};
    }

    @Override
    public void onClick(View v)
    {

        if (v.getId() == R.id.plus) {

            currentValue += increment;

            if (currentValue > max)
                currentValue = max;

        } else if (v.getId() == R.id.minus) {

            currentValue -= increment;

            if (currentValue < min)
                currentValue = min;
        }

        ((TextView) findViewById(R.id.value)).setText(Integer.toString(currentValue));
    }
}
