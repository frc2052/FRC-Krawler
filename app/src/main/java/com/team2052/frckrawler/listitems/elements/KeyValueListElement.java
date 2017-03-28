package com.team2052.frckrawler.listitems.elements;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.listitems.ListElement;

import java.util.Map;

/**
 * Created by Adam on 6/3/2016.
 */

public class KeyValueListElement extends ListElement {
    String key, value;

    public KeyValueListElement(Map.Entry<String, String> entry) {
        this.key = entry.getKey();
        this.value = entry.getValue();
    }

    public KeyValueListElement(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public View getView(Context c, LayoutInflater inflater, View convertView) {
        View v = inflater.inflate(R.layout.key_value_list_item, null, false);
        ((TextView) v.findViewById(R.id.key_text_view)).setText(key);
        ((TextView) v.findViewById(R.id.value_text_view)).setText(value);
        return v;
    }
}
