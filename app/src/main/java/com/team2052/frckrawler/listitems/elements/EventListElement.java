package com.team2052.frckrawler.listitems.elements;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.listitems.ListElement;
import com.team2052.frckrawler.tba.JSON;

import java.text.DateFormat;

public class EventListElement extends ListElement {
    private final Event event;

    public EventListElement(Event event) {
        super(Long.toString(event.getId()));
        this.event = event;
    }

    @Override
    public View getView(final Context c, LayoutInflater inflater, View convertView) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_event, null);
        }
        if (event.getData() != null && JSON.getAsJsonObject(event.getData()).has("location"))
            ((TextView) convertView.findViewById(R.id.list_view_event_location)).setText(JSON.getAsJsonObject(event.getData()).get("location").getAsString());
        else
            ((TextView) convertView.findViewById(R.id.list_view_event_location)).setText("Unknown");
        ((TextView) convertView.findViewById(R.id.list_view_event_name)).setText(event.getName());
        ((TextView) convertView.findViewById(R.id.list_view_event_date)).setText(DateFormat.getDateInstance().format(event.getDate()));
        return convertView;
    }
}
