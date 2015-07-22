package com.team2052.frckrawler.views.metric;

import android.content.Context;
import android.support.v7.widget.AppCompatCheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.database.MetricValue;
import com.team2052.frckrawler.tba.JSON;

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
        TextView name = (TextView) findViewById(R.id.name);
        name.setText(m.getMetric().getName());

        JsonObject data = JSON.getAsJsonObject(m.getMetric().getData());


        JsonArray values = data.get("values").getAsJsonArray();
        Type listType = new TypeToken<List<Integer>>() {
        }.getType();

        List<Integer> array = null;
        if (m.getValue() != null) {
            JsonObject value_data = JSON.getAsJsonObject(m.getValue());
            if (value_data.has("values") && !value_data.get("values").isJsonNull()) {
                array = JSON.getGson().fromJson(value_data.get("values"), listType);
            }
        }


        for (int i = 0; i < values.size(); i++) {
            String value = values.get(i).getAsString();
            AppCompatCheckBox checkbox = new AppCompatCheckBox(getContext());
            checkbox.setText(value);
            if (array != null) {
                for (Integer integer : array) {
                    if (i == integer) {
                        checkbox.setChecked(true);
                    }
                }
            }
            this.values.addView(checkbox);
        }

    }


    @Override
    public JsonElement getData() {
        ArrayList<Integer> index_values = new ArrayList<>();

        for (int i = 0; i < this.values.getChildCount(); i++) {
            AppCompatCheckBox check_box = (AppCompatCheckBox) this.values.getChildAt(i);
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
