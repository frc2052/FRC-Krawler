package com.team2052.frckrawler.listitems;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activity.dialog.EditUserDialogActivity;
import com.team2052.frckrawler.database.structures.User;

/**
 * Created by Adam on 8/25/2014.
 */
public class UserListItem implements ListItem {

    private final User user;

    public UserListItem(User user) {
        this.user = user;
    }

    @Override
    public View getView(final Context c, LayoutInflater inflater, View convertView) {
        convertView = inflater.inflate(R.layout.list_item_user, null);
        ((TextView) convertView.findViewById(R.id.list_item_user_name)).setText(user.getName());
        convertView.findViewById(R.id.list_item_user_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(c, EditUserDialogActivity.class);
                i.putExtra(EditUserDialogActivity.USER_ID_EXTRA, Integer.toString(user.getID()));
                c.startActivity(i);
            }
        });
        return convertView;
    }
}
