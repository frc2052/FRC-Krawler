package com.team2052.frckrawler.views.metric;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.database.MetricHelper;
import com.team2052.frckrawler.database.MetricValue;
import com.team2052.frckrawler.tba.JSON;

public class BooleanMetricWidget extends MetricWidget implements OnClickListener {
    boolean value = false;

    public BooleanMetricWidget(Context context, MetricValue m) {

        super(context, m);
        inflater.inflate(R.layout.widget_metric_boolean, this);
        ((TextView) findViewById(R.id.name)).setText(m.getMetric().getName());
        findViewById(R.id.yes).setOnClickListener(this);
        findViewById(R.id.no).setOnClickListener(this);

        if (m.getValue() != null)
            value = JSON.getAsJsonObject(m.getValue()).get("value").getAsBoolean();

        setValue(value);
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
