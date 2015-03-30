package com.team2052.frckrawler.core.listitems.elements;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.core.listitems.ListElement;
import com.team2052.frckrawler.core.tba.JSON;
import com.team2052.frckrawler.core.util.MetricUtil;
import com.team2052.frckrawler.db.Metric;

/**
 * @author Adam
 */
public class MetricListElement extends ListElement {
    private final Metric metric;
    private String descriptionString = "No Description";
    private String typeString = "";
    private String rangeString = "";

    public MetricListElement(Metric metric) {
        super(Long.toString(metric.getId()));
        this.metric = metric;
        JsonObject data = JSON.getAsJsonObject(metric.getData());

        if (!Strings.isNullOrEmpty(data.get("description").getAsString())) {
            descriptionString = data.get("description").getAsString();
        }
        Log.i("FRCKrawler", String.valueOf(metric.getType()));
        switch (metric.getType()) {
            case MetricUtil.BOOLEAN:
                typeString = "Boolean";
                rangeString = "Not Applicable";
                break;
            case MetricUtil.COUNTER:
                typeString = "Counter";
                rangeString = data.get("min").getAsString() + " to " + data.get("max").getAsString() + " Incrementing by " + data.get("inc").getAsString();
                break;
            case MetricUtil.CHECK_BOX:
                boolean isFirst = true;
                for (JsonElement value : data.get("values").getAsJsonArray()) {
                    if (!isFirst) {
                        rangeString += ", ";
                    }
                    isFirst = false;
                    rangeString += value;
                }
                typeString = "Check Box";
                break;
            case MetricUtil.CHOOSER:
                isFirst = true;
                for (JsonElement value : data.get("values").getAsJsonArray()) {
                    if (!isFirst) {
                        rangeString += ", ";
                    }
                    isFirst = false;
                    rangeString += value;
                }
                typeString = "Chooser";
                break;
            case MetricUtil.SLIDER:
                rangeString = data.get("min").getAsString() + " to " + data.get("max").getAsString();
                typeString = "Slider";
                break;
        }


    }

    @Override
    public View getView(final Context c, LayoutInflater inflater, View convertView) {
        convertView = inflater.inflate(R.layout.list_item_metric, null);
        ((TextView) convertView.findViewById(R.id.metric_list_name)).setText(metric.getName());
        ((TextView) convertView.findViewById(R.id.metric_list_description)).setText(descriptionString);
        ((TextView) convertView.findViewById(R.id.metric_list_range)).setText(rangeString);
        ((TextView) convertView.findViewById(R.id.metric_list_type)).setText(typeString);
        return convertView;
    }
}
