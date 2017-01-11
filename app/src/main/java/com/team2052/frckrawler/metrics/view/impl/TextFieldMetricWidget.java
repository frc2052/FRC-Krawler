package com.team2052.frckrawler.metrics.view.impl;

import android.content.Context;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.database.metric.MetricValue;
import com.team2052.frckrawler.util.MetricHelper;
import com.team2052.frckrawler.util.Tuple2;
import com.team2052.frckrawler.metrics.view.MetricWidget;

public class TextFieldMetricWidget extends MetricWidget {
    EditText editText;
    TextView textView;

    public TextFieldMetricWidget(Context context, MetricValue m) {
        super(context, m);
        inflater.inflate(R.layout.widget_metric_text_field, this);
        initViews();
        setMetricValue(m);
    }

    public TextFieldMetricWidget(Context context) {
        super(context);
        inflater.inflate(R.layout.widget_metric_text_field, this);
        initViews();
    }

    private void initViews() {
        editText = (EditText) findViewById(R.id.text_input);
        textView = (TextView) findViewById(R.id.title);
    }


    @Override
    public void setMetricValue(MetricValue m) {
        ((TextView) findViewById(R.id.title)).setText(m.getMetric().getName());
        Tuple2<String, MetricHelper.ReturnResult> stringMetricValue = MetricHelper.getStringMetricValue(m);

        if (stringMetricValue.t2.isError) {
            editText.setText("");
        }

        editText.setText(stringMetricValue.t1);
    }

    @Override
    public JsonElement getData() {
        return MetricHelper.buildStringMetricValue(editText.getText().toString());
    }
}
