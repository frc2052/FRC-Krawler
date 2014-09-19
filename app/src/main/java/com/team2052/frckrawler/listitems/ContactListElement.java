package com.team2052.frckrawler.listitems;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.team2052.frckrawler.ListUpdateListener;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.database.models.Contact;

import org.w3c.dom.Text;

/**
 * @author Adam
 */
public class ContactListElement extends ListElement{
    private final ListUpdateListener listener;
    private final Contact contact;

    public ContactListElement(Contact contact, ListUpdateListener listener) {
        super(Integer.toString(contact.remoteId));
        this.contact = contact;
        this.listener = listener;
    }

    @Override
    public View getView(final Context c, LayoutInflater inflater, View convertView) {
        convertView = inflater.inflate(R.layout.contact_list_item, null);
        convertView.findViewById(R.id.contact_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Edit User
            }
        });
        convertView.findViewById(R.id.contact_discard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(c);
                builder.setMessage("Are you sure you want to delete this contact?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        contact.delete();
                        listener.updateList();
                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.create().show();
            }
        });
        ((TextView)convertView.findViewById(R.id.contact_name)).setText(contact.name);
        ((TextView)convertView.findViewById(R.id.contact_team_role)).setText(contact.teamRole);
        return convertView;
    }
}
