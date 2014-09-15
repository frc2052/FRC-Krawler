package com.team2052.frckrawler.listitems;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.database.models.Event;
import com.team2052.frckrawler.activity.MatchListActivity;

public class EventListItem implements ListItem {
    private final Event event;

    public EventListItem(Event event) {
        this.event = event;
    }

    @Override
    public View getView(final Context c, LayoutInflater inflater, View convertView) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_event, null);
        }
        final PopupMenu menu = new PopupMenu(c, convertView.findViewById(R.id.list_view_event_edit_button));
        //Menu Items
        menu.getMenu().add(Menu.NONE, R.id.event_more_summary, Menu.NONE, "Summary");
        menu.getMenu().add(Menu.NONE, R.id.event_more_schedule, Menu.NONE, "Schedule");
        menu.getMenu().add(Menu.NONE, R.id.event_more_schedule, Menu.NONE, "Match Data");
        menu.getMenu().add(Menu.NONE, R.id.event_more_robots, Menu.NONE, "Robots");
        //On Menu Item Clicked
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //TODO
                switch (item.getItemId()){
                    case R.id.event_more_schedule:
                        c.startActivity(MatchListActivity.newInstance(c, event));
                        break;
                }
                return true;
            }
        });
        //When the view is pressed open the menu that it is anchored to
        convertView.findViewById(R.id.list_view_event_edit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.show();
            }
        });
        ((TextView) convertView.findViewById(R.id.list_view_event_location)).setText(event.location);
        //((TextView) convertView.findViewById(R.id.list_view_event_date)).setText(Long.toString(event.date.getTime()));
        ((TextView) convertView.findViewById(R.id.list_view_event_name)).setText(event.name);
        return convertView;
    }
}
