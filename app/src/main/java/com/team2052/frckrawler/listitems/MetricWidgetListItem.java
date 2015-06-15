package com.team2052.frckrawler.listitems;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.team2052.frckrawler.views.metric.MetricWidget;

/**
 * Created by Adam on 6/6/2015.
 */
public class MetricWidgetListItem implements ListItem {

    private MetricWidget widget;

    public MetricWidgetListItem(MetricWidget widget) {
        this.widget = widget;
    }

    @Override
    public View getView(Context c, LayoutInflater inflater, View convertView) {
        return widget;
    }
}
