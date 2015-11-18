package com.team2052.frckrawler.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.team2052.frckrawler.listitems.ListItem;

import java.util.List;

public class ListViewAdapter extends ArrayAdapter<ListItem> {
    private final LayoutInflater inflater;
    protected List<ListItem> values;

    public ListViewAdapter(Context context, List<ListItem> values) {
        super(context, android.R.layout.simple_list_item_1, values);
        this.values = values;
        inflater = LayoutInflater.from(context);
    }

    public void removeAt(int index) {
        if (index >= 0) {
            values.remove(index);
            updateListData();
        }
    }

    public void updateListData() {
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getItem(position).getView(getContext(), inflater, convertView);
    }
}