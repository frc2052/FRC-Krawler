package com.team2052.frckrawler.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.EditText;

import com.team2052.frckrawler.FRCKrawler;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.db.User;
import com.team2052.frckrawler.listeners.ListUpdateListener;

/**
 * @author Adam
 */
public class AddUserDialogFragment extends DialogFragment
{

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        final View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_dialog_add_user, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add User");
        builder.setView(view);
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                ((FRCKrawler) getActivity().getApplication()).getDaoSession().getUserDao().insert(new User(null, ((EditText) view.findViewById(R.id.name)).getText().toString().trim()));
                ((ListUpdateListener) getParentFragment()).updateList();
            }
        });
        builder.setNegativeButton("Cancel", null);
        return builder.create();
    }
}
