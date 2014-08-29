package com.team2052.frckrawler.listitems;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activity.MetricsActivity;
import com.team2052.frckrawler.activity.dialog.EditMetricDialogActivity;
import com.team2052.frckrawler.database.DBContract;
import com.team2052.frckrawler.database.models.Metric;

/**
 * Created by Adam on 8/23/2014.
 */
public class MetricListElement implements ListItem {
    private final String descripstionString;
    private final String isDisplayed;
    private final Metric metric;
    private String typeString = "";
    private String rangeString = "";

    public MetricListElement(Metric metric) {
        this.metric = metric;
        Object[] rangeArr = metric.parseRange();
        isDisplayed = Boolean.toString(metric.display);
        if (metric.description.equals("")) {
            descripstionString = "No Description";
        } else {
            descripstionString = metric.description;
        }
        switch (metric.type) {
            case Metric.BOOLEAN:
                typeString = "Boolean";
                rangeString = "Not Applicable";
                break;
            case DBContract.TEXT:
                typeString = "Text";
                rangeString = "Not Applicable";
                break;
            case DBContract.COUNTER:
                typeString = "Counter";
                rangeString = rangeArr[0] + " to " + rangeArr[1] + " Incrementing by " + rangeArr[2];
                break;
            case DBContract.CHOOSER:
                for (Object o : rangeArr) {
                    rangeString += ", ";
                    rangeString += o;
                }
                typeString = "Chooser";
                break;
            case DBContract.SLIDER:
                rangeString = rangeArr[0] + " to " + rangeArr[1];
                typeString = "Slider";
                break;
            case DBContract.MATH:
                rangeString = "";
                typeString = "Math";
                break;
        }
    }

    @Override
    public View getView(final Context c, LayoutInflater inflater, View convertView) {
        convertView = inflater.inflate(R.layout.list_item_metric, null);
        ((TextView) convertView.findViewById(R.id.metric_list_name)).setText(metric.name);
        ((TextView) convertView.findViewById(R.id.metric_list_description)).setText(descripstionString);
        ((TextView) convertView.findViewById(R.id.metric_list_displayed)).setText(isDisplayed);
        ((TextView) convertView.findViewById(R.id.metric_list_range)).setText(rangeString);
        ((TextView) convertView.findViewById(R.id.metric_list_type)).setText(typeString);
        ((ImageView) convertView.findViewById(R.id.metric_list_edit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {c.startActivity(EditMetricDialogActivity.newInstance(c, metric));    }
        });
        return convertView;
    }
}
