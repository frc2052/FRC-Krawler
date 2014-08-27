package com.team2052.frckrawler.listitems;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Adam on 8/22/2014.
 */
public class SimpleListElement extends ListElement {
    private final String text;

    public SimpleListElement(String name) {
        super(name);
        this.text = name;
    }

    @Override
    public View getView(Context c, LayoutInflater inflater, View convertView) {
        View view = inflater.inflate(android.R.layout.simple_expandable_list_item_1, null);
        TextView text = (TextView) view.findViewById(android.R.id.text1);
        text.setPadding(8, 8, 8, 8);
        text.setText(this.text);
        text.setTextSize(40);
        return view;
    }
}
