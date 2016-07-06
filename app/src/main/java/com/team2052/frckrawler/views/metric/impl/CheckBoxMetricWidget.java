package com.team2052.frckrawler.views.metric.impl;

import android.content.Context;
import android.support.v7.widget.AppCompatCheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.common.base.Optional;
import com.google.gson.JsonElement;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.database.metric.MetricHelper;
import com.team2052.frckrawler.database.metric.MetricValue;
import com.team2052.frckrawler.util.Tuple2;
import com.team2052.frckrawler.views.metric.MetricWidget;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by adam on 3/28/15.
 */
public class CheckBoxMetricWidget extends MetricWidget {
    private LinearLayout values;

    public CheckBoxMetricWidget(Context context, MetricValue m) {
        super(context, m);
        inflater.inflate(R.layout.widget_metric_checkbox, this);
        this.values = (LinearLayout) findViewById(R.id.values);

        final Optional<List<String>> optionalValues = MetricHelper.getListItemIndexRange(m.getMetric());
        if (!optionalValues.isPresent())
            throw new IllegalStateException("Couldn't parse range values, cannot proceed");
        final List<String> rangeValues = optionalValues.get();

        for (int i = 0; i < rangeValues.size(); i++) {
            String value = rangeValues.get(i);
            AppCompatCheckBox checkbox = new AppCompatCheckBox(getContext());
            checkbox.setText(value);
            this.values.addView(checkbox);
        }
        setMetricValue(m);
    }

    public CheckBoxMetricWidget(Context context) {
        super(context);
        inflater.inflate(R.layout.widget_metric_checkbox, this);
    }

    @Override
    public void setMetricValue(MetricValue m) {
        TextView name = (TextView) findViewById(R.id.name);
        name.setText(m.getMetric().getName());

        final Tuple2<List<Integer>, MetricHelper.ReturnResult> preLoadedValuesResult = MetricHelper.getListIndexMetricValue(m);

        for (int i = 0; i < values.getChildCount(); i++) {
            if (preLoadedValuesResult.t1.contains(i)) {
                ((AppCompatCheckBox) values.getChildAt(i)).setChecked(true);
            } else {
                ((AppCompatCheckBox) values.getChildAt(i)).setChecked(false);
            }
        }
    }


    @Override
    public JsonElement getData() {
        return MetricHelper.buildListIndexValue(getIndexValues());
    }

    public List<Integer> getIndexValues() {
        ArrayList<Integer> index_values = new ArrayList<>();

        for (int i = 0; i < this.values.getChildCount(); i++) {
            AppCompatCheckBox check_box = (AppCompatCheckBox) this.values.getChildAt(i);
            if (check_box.isChecked()) {
                index_values.add(i);
            }
        }

        return index_values;
    }
}
