package com.team2052.frckrawler.core.ui.metric;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.core.database.MetricValue;
import com.team2052.frckrawler.core.tba.JSON;

import java.util.ArrayList;
import java.util.List;

public class ChooserMetricWidget extends MetricWidget implements OnItemSelectedListener {

    private final Spinner chooserSpinner;
    private final List<String> range;
    private final ArrayAdapter<Object> adapter;
    String value;

    public ChooserMetricWidget(Context context, MetricValue m) {

        super(context, m);
        inflater.inflate(R.layout.widget_metric_chooser, this);

        if (m.getValue() != null)
            value = JSON.getAsJsonObject(m.getValue()).get("value").getAsString();

        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1);

        JsonArray range = JSON.getAsJsonArray(m.getMetric().getData());
        List<String> rangeValues = new ArrayList<>();

        for (JsonElement jsonElement : range) {
            JsonObject object = jsonElement.getAsJsonObject();
            rangeValues.add(object.get("value").getAsString());
        }

        this.range = rangeValues;

        int selectedPos = 0;

        for (int i = 0; i < range.size(); i++) {
            adapter.add(rangeValues.get(i));
            if (value != null && value.equals(rangeValues.get(i)))
                selectedPos = i;
        }

        chooserSpinner = (Spinner) findViewById(R.id.choooserList);
        chooserSpinner.setAdapter(adapter);
        chooserSpinner.setOnItemSelectedListener(this);
        if (!adapter.isEmpty())
            chooserSpinner.setSelection(selectedPos);
        ((TextView) findViewById(R.id.name)).setText(m.getMetric().getName());
    }

    @Override
    public void onItemSelected(AdapterView<?> a, View arg1, int arg2, long arg3) {
        value = (String) a.getSelectedItem();
    }

    @Override
    public void onNothingSelected(AdapterView<?> a) {
        a.setSelection(0);
    }

    @Override
    public JsonElement getData() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("value", value);
        return jsonObject;
    }
}
