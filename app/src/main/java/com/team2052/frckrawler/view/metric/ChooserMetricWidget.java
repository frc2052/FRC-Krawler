package com.team2052.frckrawler.view.metric;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.database.MetricValue;
import com.team2052.frckrawler.database.serializers.StringArrayDeserializer;

public class ChooserMetricWidget extends MetricWidget implements OnItemSelectedListener
{

    private String value;

    public ChooserMetricWidget(Context context, MetricValue m)
    {

        super(context, m.getMetric(), m.getValue());
        inflater.inflate(R.layout.widget_metric_chooser, this);

        if (m.getValue() != null)
            value = m.getValue();

        ArrayAdapter<Object> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1);
        String[] range = StringArrayDeserializer.deserialize(m.getMetric().getRange());
        int selectedPos = 0;
        for (int i = 0; i < range.length; i++) {
            adapter.add(range[i]);
            if (value != null && value.toString().equals(range[i]))
                selectedPos = i;
        }

        Spinner chooserSpinner = (Spinner) findViewById(R.id.choooserList);
        chooserSpinner.setAdapter(adapter);
        chooserSpinner.setOnItemSelectedListener(this);
        if (!adapter.isEmpty())
            chooserSpinner.setSelection(selectedPos);
        ((TextView) findViewById(R.id.name)).setText(m.getMetric().getName());
    }

    @Override
    public String getValues()
    {
        return value;
    }

    @Override
    public void onItemSelected(AdapterView<?> a, View arg1, int arg2,
                               long arg3)
    {
        value = (String) a.getSelectedItem();
    }

    @Override
    public void onNothingSelected(AdapterView<?> a)
    {
        a.setSelection(0);
    }
}
