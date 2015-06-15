package com.team2052.frckrawler.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.team2052.frckrawler.listitems.ListItem;
import com.team2052.frckrawler.listitems.items.NavDrawerItem;

import java.util.List;

public class NavDrawerAdataper extends ListViewAdapter {
    private int mSelectedItemPosition = -1;

    public NavDrawerAdataper(Context context, List<ListItem> navDrawerItems) {
        super(context, navDrawerItems);
    }

    public void setItemSelected(int position) {
        this.mSelectedItemPosition = position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }

    public int getPositionForId(int id) {
        for (int i = 0; i < getCount(); i++) {
            if (getItem(i).getId() == id) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public NavDrawerItem getItem(int position) {
        return (NavDrawerItem) super.getItem(position);
    }

}
