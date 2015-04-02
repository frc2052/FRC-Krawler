package com.team2052.frckrawler.core.listitems.elements;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.team2052.frckrawler.core.listitems.ListElement;
import com.team2052.frckrawler.core.ui.PickListView;
import com.team2052.frckrawler.db.PickList;

/**
 * Created by adam on 3/30/15.
 */
public class PickListListElement extends ListElement {
    private PickList pickList;

    public PickListListElement(PickList pickList) {
        super(String.valueOf(pickList.getId()));
        this.pickList = pickList;
    }

    @Override
    public View getView(Context c, LayoutInflater inflater, View convertView) {
        convertView = new PickListView(c);
        ((PickListView) convertView).initWithParams(pickList);
        return convertView;
    }
}
