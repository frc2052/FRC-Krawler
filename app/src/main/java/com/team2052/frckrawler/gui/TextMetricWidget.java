package com.team2052.frckrawler.gui;

import android.content.Context;
import android.text.*;
import android.widget.*;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.database.MetricValue;

public class TextMetricWidget extends MetricWidget implements TextWatcher
{

    private String textVal;

    public TextMetricWidget(Context context, MetricValue m)
    {

        super(context, m.getMetric(), m.getValue());
        inflater.inflate(R.layout.widget_metric_text, this);

        if (m.getValue() != null && m.getValue().length > 0)
            textVal = m.getValue()[0];
        else
            textVal = new String();

        ((EditText) findViewById(R.id.text)).addTextChangedListener(this);
        ((EditText) findViewById(R.id.text)).setText(textVal);
        ((TextView) findViewById(R.id.name)).setText(m.getMetric().name);
    }

    @Override
    public String[] getValues()
    {

        return new String[]{textVal};
    }

    @Override
    public void afterTextChanged(Editable e)
    {

        textVal = e.toString();
    }

    @Override
    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3)
    {
    }

    @Override
    public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3)
    {
    }
}
