package com.team2052.frckrawler.listitems;

import android.content.Context;
import android.view.*;

public interface ListItem
{
    public View getView(Context c, LayoutInflater inflater, View convertView);
}
