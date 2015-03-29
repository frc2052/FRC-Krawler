package com.team2052.frckrawler.core.ui.metric;

import android.content.Context;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.core.database.MetricValue;
import com.team2052.frckrawler.core.tba.JSON;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by adam on 3/28/15.
 */
public class CheckBoxMetricWidget extends MetricWidget {


    private final LinearLayout values;

    public CheckBoxMetricWidget(Context context, MetricValue m) {
        super(context, m);
        inflater.inflate(R.layout.widget_metric_checkbox, this);
        this.values = (LinearLayout) findViewById(R.id.values);
        JsonObject data = JSON.getAsJsonObject(m.getMetric().getData());


        JsonArray values = data.get("values").getAsJsonArray();
        Type listType = new TypeToken<List<Integer>>() {
        }.getType();
        List<Integer> array = null;
        if (m.getValue() != null) {
            JsonObject value_data = JSON.getAsJsonObject(m.getValue());
            array = JSON.getGson().fromJson(value_data.get("values"), listType);
        }


        for (int i = 0; i < values.size(); i++) {
            String value = values.get(i).getAsString();
            CheckBox checkbox = new CheckBox(getContext());
            checkbox.setText(value);
            if (array != null) {
                for (Integer integer : array) {
                    if (i == integer) {
                        checkbox.setChecked(true);
                    }
                }
            }
            Log.i("FRCKrawler", String.valueOf(i) + String.valueOf(checkbox.isChecked()));
            this.values.addView(checkbox);
        }

    }


    @Override
    public JsonElement getData() {
        Log.i("FRCKrawler", "Saving");
        ArrayList<Integer> index_values = new ArrayList<>();

        for (int i = 0; i < this.values.getChildCount(); i++) {
            CheckBox check_box = (CheckBox) this.values.getChildAt(i);
            Log.i("FRCKrawler", String.valueOf(i) + String.valueOf(check_box.isChecked()));
            if (check_box.isChecked()) {
                index_values.add(i);
            }
        }

        JsonObject data = new JsonObject();
        JsonElement values = JSON.getGson().toJsonTree(index_values);

        data.add("values", values);
        return data;
    }
}
