package com.team2052.frckrawler.listitems;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

public interface ListItem {
    public View getView(Context c, LayoutInflater inflater, View convertView);
}
