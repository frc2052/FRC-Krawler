package com.team2052.frckrawler.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.listitems.NavDrawerItem;

public class NavDrawerAdataper extends ListViewAdapter {
    private int mSelectedItemPosition = -1;

    public NavDrawerAdataper(Context context) {
        super(context, com.team2052.frckrawler.fragment.NavigationDrawerFragment.NAV_ITEMS);
    }

    public void setItemSelected(int position) {
        this.mSelectedItemPosition = position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = super.getView(position, convertView, parent);
        boolean isSelectedItem = (position == mSelectedItemPosition);
        TextView text = (TextView) v.findViewById(R.id.title);
        if (text != null) {
            text.setTypeface(null, isSelectedItem ? Typeface.BOLD : Typeface.NORMAL);
        }
        return v;
    }

    @Override
    public NavDrawerItem getItem(int position) {
        return (NavDrawerItem) super.getItem(position);
    }


    public int getPositionForId(int id) {
        for (int i = 0; i < getCount(); i++) {
            if (getItem(i).getId() == id) {
                return i;
            }
        }
        return -1;
    }

}
