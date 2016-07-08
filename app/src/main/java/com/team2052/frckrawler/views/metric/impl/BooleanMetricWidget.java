package com.team2052.frckrawler.views.metric.impl;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.common.base.Optional;
import com.google.gson.JsonElement;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.database.metric.MetricHelper;
import com.team2052.frckrawler.database.metric.MetricValue;
import com.team2052.frckrawler.views.metric.MetricWidget;

public class BooleanMetricWidget extends MetricWidget implements OnClickListener {
    private boolean value = false;

    public BooleanMetricWidget(Context context, MetricValue m) {
        super(context, m);
        inflater.inflate(R.layout.widget_metric_boolean, this);
        setMetricValue(m);
    }

    public BooleanMetricWidget(Context context) {
        super(context);
        inflater.inflate(R.layout.widget_metric_boolean, this);
    }

    @Override
    public void setMetricValue(MetricValue m) {
        ((TextView) findViewById(R.id.name)).setText(m.getMetric().getName());
        findViewById(R.id.yes).setOnClickListener(this);
        findViewById(R.id.no).setOnClickListener(this);

        final Optional<Boolean> optionalValue = MetricHelper.getBooleanValue(m);
        if (optionalValue.isPresent())
            setValue(optionalValue.get());
        else
            setValue(false);
    }

    public void setValue(boolean value) {
        this.value = value;
        ((RadioButton) findViewById(R.id.yes)).setChecked(value);
        ((RadioButton) findViewById(R.id.no)).setChecked(!value);
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
        return MetricHelper.buildBooleanMetricValue(value);
    }
}
