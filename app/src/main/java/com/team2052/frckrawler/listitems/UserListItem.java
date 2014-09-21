package com.team2052.frckrawler.listitems;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.database.models.User;
import com.team2052.frckrawler.fragment.dialog.EditUserDialogFragment;

/**
 * @author Adam
 */
public class UserListItem implements ListItem {

    private final User user;

    public UserListItem(User user) {
        this.user = user;
    }

    @Override
    public View getView(final Context c, LayoutInflater inflater, View convertView) {
        convertView = inflater.inflate(R.layout.list_item_user, null);
        ((TextView) convertView.findViewById(R.id.list_item_user_name)).setText(user.name);
        convertView.findViewById(R.id.list_item_user_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditUserDialogFragment fragment = EditUserDialogFragment.newInstance(user);
                fragment.show(((FragmentActivity) c).getSupportFragmentManager(), "Edit User");
                /*Intent i = new Intent(c, EditUserDialogActivity.class);
                i.putExtra(EditUserDialogActivity.USER_ID_EXTRA, Integer.toString(1));
                c.startActivity(i);*/
            }
        });
        return convertView;
    }
}
