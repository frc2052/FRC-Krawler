package com.team2052.frckrawler.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nhaarman.listviewanimations.ArrayAdapter;
import com.team2052.frckrawler.listitems.ListItem;

import java.util.List;

public class ListViewAdapter extends ArrayAdapter<ListItem> {
    private final List<ListItem> values;
    private final LayoutInflater inflater;
    private final Context context;


    public ListViewAdapter(Context context, List<ListItem> values) {
        super(values);
        this.values = values;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void removeAt(int index) {
        if (index >= 0) {
            values.remove(index);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getItem(position).getView(context, inflater, convertView);
    }

    public void updateListData() {
        notifyDataSetChanged();
    }

}
