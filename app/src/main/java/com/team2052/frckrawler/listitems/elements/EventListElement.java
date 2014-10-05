package com.team2052.frckrawler.listitems.elements;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.database.models.Event;
import com.team2052.frckrawler.listitems.ListElement;
import com.team2052.frckrawler.tba.types.EventDeserializer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class EventListElement extends ListElement
{
    private final Event event;

    public EventListElement(Event event)
    {
        super(Long.toString(event.getId()));
        this.event = event;
    }

    @Override
    public View getView(final Context c, LayoutInflater inflater, View convertView)
    {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_event, null);
        }
        ((TextView) convertView.findViewById(R.id.list_view_event_date)).setText(event.date != null ? EventDeserializer.format.format(event.date) : "");
        ((TextView) convertView.findViewById(R.id.list_view_event_location)).setText(event.location);
        ((TextView) convertView.findViewById(R.id.list_view_event_name)).setText(event.name);
        ((TextView)convertView.findViewById(R.id.list_view_event_date)).setText(DateFormat.getDateInstance().format(event.date));
        return convertView;
    }
}
