package com.team2052.frckrawler.gui;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.database.MetricValue;

public class BooleanMetricWidget extends MetricWidget implements OnClickListener
{

    private boolean value;

    public BooleanMetricWidget(Context context, MetricValue m)
    {

        super(context, m.getMetric(), m.getValue());
        inflater.inflate(R.layout.widget_metric_boolean, this);
        ((TextView) findViewById(R.id.name)).setText(m.getMetric().name);
        findViewById(R.id.yes).setOnClickListener(this);
        findViewById(R.id.no).setOnClickListener(this);

        if (m.getValue() != null && m.getValue().length > 0)
            value = Boolean.parseBoolean(m.getValue()[0]);

        if (value) {
            ((RadioButton) findViewById(R.id.yes)).setChecked(true);
            ((RadioButton) findViewById(R.id.no)).setChecked(false);
        } else {
            ((RadioButton) findViewById(R.id.yes)).setChecked(false);
            ((RadioButton) findViewById(R.id.no)).setChecked(true);
        }
    }

    @Override
    public String[] getValues()
    {
        return new String[]{Boolean.toString(value)};
    }

    @Override
    public void onClick(View view)
    {

        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()) {
            case R.id.yes:
                if (checked)
                    value = true;

                break;

            case R.id.no:
                if (checked)
                    value = false;
        }
    }
}
