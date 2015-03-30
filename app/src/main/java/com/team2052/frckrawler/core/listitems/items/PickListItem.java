package com.team2052.frckrawler.core.listitems.items;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.core.listitems.ListItem;

/**
 * Created by adam on 3/29/15.
 */
public class PickListItem implements ListItem {
    private int team_number;
    private int prio;


    public PickListItem(int team_number, int prio) {
        this.team_number = team_number;
        this.prio = prio;
    }

    @Override
    public View getView(Context c, LayoutInflater inflater, View convertView) {
        convertView = inflater.inflate(R.layout.pick_list_item, null);
        TextView header = (TextView) convertView.findViewById(R.id.pick_list_title);
        TextView priority = (TextView) convertView.findViewById(R.id.pick_list_item_priority);
        ((CheckBox) convertView.findViewById(R.id.pick_list_item_check_box)).setOnCheckedChangeListener((buttonView, isChecked) -> {
            header.setEnabled(!isChecked);
            priority.setEnabled(!isChecked);
        });
        return convertView;
    }
}
