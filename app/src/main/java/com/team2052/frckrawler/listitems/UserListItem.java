package com.team2052.frckrawler.listitems;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.database.models.User;

/**
 * @author Adam
 */
public class UserListItem extends ListElement
{

    private final User user;

    public UserListItem(User user)
    {
        super(Long.toString(user.getId()));
        this.user = user;
    }

    @Override
    public View getView(final Context c, LayoutInflater inflater, View convertView)
    {
        convertView = inflater.inflate(R.layout.list_item_user, null);
        ((TextView) convertView.findViewById(R.id.list_item_user_name)).setText(user.name);
        return convertView;
    }
}
