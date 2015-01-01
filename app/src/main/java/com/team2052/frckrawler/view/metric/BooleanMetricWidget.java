package com.team2052.frckrawler.view.metric;

import android.content.Context;
import android.os.Parcelable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.database.MetricValue;
import com.team2052.frckrawler.tba.JSON;

public class BooleanMetricWidget extends MetricWidget implements OnClickListener {
    boolean value = false;

    public BooleanMetricWidget(Context context, MetricValue m) {

        super(context, m.getMetric(), m.getValue());
        inflater.inflate(R.layout.widget_metric_boolean, this);
        ((TextView) findViewById(R.id.name)).setText(m.getMetric().getName());
        findViewById(R.id.yes).setOnClickListener(this);
        findViewById(R.id.no).setOnClickListener(this);

        if (m.getValue() != null)
            value = JSON.getAsJsonObject(m.getValue()).get("value").getAsBoolean();

        if (value) {
            ((RadioButton) findViewById(R.id.yes)).setChecked(true);
            ((RadioButton) findViewById(R.id.no)).setChecked(false);
        } else {
            ((RadioButton) findViewById(R.id.yes)).setChecked(false);
            ((RadioButton) findViewById(R.id.no)).setChecked(true);
        }
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
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof MetricWidgetSavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        MetricWidgetSavedState ss = (MetricWidgetSavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        value = Boolean.parseBoolean(ss.value);
    }

    @Override
    public MetricValue getMetricValue() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("value", value);
        return new MetricValue(getMetric(), JSON.getGson().toJson(jsonObject));
    }
}
