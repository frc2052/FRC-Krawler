package com.team2052.frckrawler.listitems.items;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.listitems.ListItem;
import com.team2052.frckrawler.views.NavDrawerImageView;

public class NavDrawerItem implements ListItem {
    private final String title;
    private int icon = -1;
    private int layout;
    private int id;

    public NavDrawerItem(String title) {
        this.title = title;
    }

    public NavDrawerItem(int id, String title, int icon) {
        this.id = id;
        this.title = title;
        this.icon = icon;
        this.layout = R.layout.nav_list_item;
    }

    @Override
    public View getView(Context c, LayoutInflater inflater, View convertView) {
        if (convertView == null) {
            convertView = inflater.inflate(layout, null);
        }
        if (icon != -1) {
            ((NavDrawerImageView) convertView.findViewById(R.id.icon)).setImageResource(icon);
        }
        ((TextView) convertView.findViewById(R.id.title)).setText(title);
        return convertView;
    }

    public int getId() {
        return id;
    }
}
