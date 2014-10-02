package com.team2052.frckrawler.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.EditText;

import com.team2052.frckrawler.ListUpdateListener;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activity.DatabaseActivity;
import com.team2052.frckrawler.database.models.Contact;
import com.team2052.frckrawler.database.models.Team;

/**
 * @author Adam
 */
public class AddContactDialogFragment extends DialogFragment
{
    private Team mTeam;

    public static AddContactDialogFragment newInstance(Team team)
    {
        AddContactDialogFragment fragment = new AddContactDialogFragment();
        Bundle b = new Bundle();
        b.putLong(DatabaseActivity.PARENT_ID, team.getId());
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mTeam = Team.load(Team.class, getArguments().getLong(DatabaseActivity.PARENT_ID, 0));
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        final View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_dialog_add_contact, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dismiss();
            }
        });
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                String name = ((EditText) view.findViewById(R.id.nameVal)).getText().toString();
                String email = ((EditText) view.findViewById(R.id.email)).getText().toString();
                String phone = ((EditText) view.findViewById(R.id.phone)).getText().toString();
                String address = ((EditText) view.findViewById(R.id.address)).getText().toString();
                String teamRole = ((EditText) view.findViewById(R.id.teamRole)).getText().toString();
                new Contact(mTeam, name, email, address, teamRole, phone).save();
                ((ListUpdateListener) getParentFragment()).updateList();
            }
        });
        builder.setTitle("Add Contact");
        return builder.create();
    }
}
