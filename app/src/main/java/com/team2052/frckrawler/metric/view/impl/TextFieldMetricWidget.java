package com.team2052.frckrawler.metric.view.impl;

import android.content.Context;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.helpers.Tuple2;
import com.team2052.frckrawler.helpers.metric.MetricDataHelper;
import com.team2052.frckrawler.metric.data.MetricValue;
import com.team2052.frckrawler.metric.view.MetricWidget;

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

    @Override
    public void initViews() {
        editText = (EditText) findViewById(R.id.text_input);
        textView = (TextView) findViewById(R.id.title);
    }


    @Override
    public void setMetricValue(MetricValue m) {
        ((TextView) findViewById(R.id.title)).setText(m.metric().getName());
        Tuple2<String, MetricDataHelper.ReturnResult> stringMetricValue = MetricDataHelper.getStringMetricValue(m);

        if (stringMetricValue.t2.isError) {
            editText.setText("");
        }

        editText.setText(stringMetricValue.t1);
    }

    @Override
    public JsonElement getData() {
        return MetricDataHelper.buildStringMetricValue(editText.getText().toString());
    }
}
