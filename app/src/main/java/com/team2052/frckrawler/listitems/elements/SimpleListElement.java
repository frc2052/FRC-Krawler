package com.team2052.frckrawler.listitems.elements;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.team2052.frckrawler.listitems.ListElement;

/**
 * @author Adam
 */
public class SimpleListElement extends ListElement {
    private final String name;

    public SimpleListElement(String name, String key) {
        super(key);
        this.name = name;
    }

    @Override
    public View getView(Context c, LayoutInflater inflater, View convertView) {
        if (convertView == null) {
            convertView = inflater.inflate(android.R.layout.simple_list_item_1, null);
        }
        //Set the name based on the key
        TextView text = (TextView) convertView.findViewById(android.R.id.text1);
        text.setMinHeight(48);
        text.setPadding(16, 16, 16, 16);
        text.setText(name);
        return convertView;
    }

    //No Idea why a spinner does to string instead of get view -- quick fix
    @Override
    public String toString() {
        return name;
    }
}
