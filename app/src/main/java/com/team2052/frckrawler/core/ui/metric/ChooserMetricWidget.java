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

public class ChooserMetricWidget extends MetricWidget implements OnItemSelectedListener {

    private final Spinner chooserSpinner;
    private final ArrayAdapter<Object> adapter;
    int value;

    public ChooserMetricWidget(Context context, MetricValue m) {

        super(context, m);
        inflater.inflate(R.layout.widget_metric_chooser, this);

        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1);

        JsonArray range = JSON.getAsJsonObject(m.getMetric().getData()).get("values").getAsJsonArray();

        int selectedPos = 0;

        if (m.getMetric() != null) {
            JsonObject json_data = JSON.getAsJsonObject(m.getValue());
            if (json_data.has("values") && !json_data.get("values").isJsonNull()) {
                JsonArray values = json_data.get("values").getAsJsonArray();
                if (values.size() > 0) {
                    selectedPos = values.get(0).getAsInt();
                }
            }

        }

        for (int i = 0; i < range.size(); i++) {
            adapter.add(range.get(i).getAsString());
        }

        chooserSpinner = (Spinner) findViewById(R.id.choooserList);
        chooserSpinner.setAdapter(adapter);
        chooserSpinner.setOnItemSelectedListener(this);
        if (!adapter.isEmpty())
            chooserSpinner.setSelection(selectedPos);
        ((TextView) findViewById(R.id.name)).setText(m.getMetric().getName());
    }

    @Override
    public void onItemSelected(AdapterView<?> a, View arg1, int pos, long arg3) {
        value = pos;
    }

    @Override
    public void onNothingSelected(AdapterView<?> a) {
        a.setSelection(0);
    }

    @Override
    public JsonElement getData() {
        int[] ints = {value};
        JsonObject data = new JsonObject();
        JsonElement values = JSON.getGson().toJsonTree(ints);
        data.add("values", values);
        return data;
    }
}
