package com.team2052.frckrawler.metric.view.impl;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.common.base.Optional;
import com.google.gson.JsonElement;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.helpers.metric.MetricDataHelper;
import com.team2052.frckrawler.metric.data.MetricValue;
import com.team2052.frckrawler.metric.view.MetricWidget;

public class BooleanMetricWidget extends MetricWidget implements OnClickListener {
    TextView name;
    RadioButton yesRadioButton, noRadioButton;
    private boolean value = false;

    public BooleanMetricWidget(Context context, MetricValue m) {
        super(context, m);
        setMetricValue(m);
    }

    public BooleanMetricWidget(Context context) {
        super(context);
    }

    @Override
    public void setMetricValue(MetricValue m) {
        name.setText(m.metric().getName());
        findViewById(R.id.yes).setOnClickListener(this);
        findViewById(R.id.no).setOnClickListener(this);

        final Optional<Boolean> optionalValue = MetricDataHelper.getBooleanValue(m);
        if (optionalValue.isPresent())
            setValue(optionalValue.get());
        else
            setValue(false);
    }

    @Override
    public void initViews() {
        inflater.inflate(R.layout.widget_metric_boolean, this);
        name = (TextView) findViewById(R.id.name);
        yesRadioButton = (RadioButton) findViewById(R.id.yes);
        noRadioButton = (RadioButton) findViewById(R.id.no);
    }

    public void setValue(boolean value) {
        this.value = value;

        yesRadioButton.setChecked(value);
        noRadioButton.setChecked(!value);
    }

    @Override
    public void onClick(View view) {
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

    @Override
    public JsonElement getData() {
        return MetricDataHelper.buildBooleanMetricValue(value);
    }
}
