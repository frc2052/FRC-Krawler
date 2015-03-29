package com.team2052.frckrawler.core.listitems.elements;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

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
    private final String descriptionString = "No Description";
    private final Metric metric;
    private String typeString = "";
    private String rangeString = "";

    public MetricListElement(Metric metric) {
        super(Long.toString(metric.getId()));
        this.metric = metric;

        if (metric.getData() != null) {
            //descriptionString = "No Description";
        } /*else {
            //descriptionString = metric.getDescription();
        }*/

        switch (metric.getType()) {
            case MetricUtil.BOOLEAN:
                typeString = "Boolean";
                rangeString = "Not Applicable";
                break;
            case MetricUtil.COUNTER:
                typeString = "Counter";
                JsonObject jsonObject = JSON.getAsJsonObject(metric.getData());
                rangeString = jsonObject.get("min").getAsString() + " to " + jsonObject.get("max").getAsString() + " Incrementing by " + jsonObject.get("inc").getAsString();
                break;
            case MetricUtil.CHOOSER:
                boolean isFirst = true;
                /*for (Object o : rangeArr) {
                    if (!isFirst) {
                        rangeString += ", ";
                    }
                    isFirst = false;
                    rangeString += o;
                }*/
                typeString = "Chooser";
                break;
            case MetricUtil.SLIDER:
                //rangeString = rangeArr[0] + " to " + rangeArr[1];
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
