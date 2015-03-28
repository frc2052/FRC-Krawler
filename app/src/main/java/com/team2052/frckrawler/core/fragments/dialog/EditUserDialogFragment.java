package com.team2052.frckrawler.core.fragments.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.EditText;

import com.team2052.frckrawler.core.FRCKrawler;
import com.team2052.frckrawler.core.listeners.ListUpdateListener;
import com.team2052.frckrawler.db.User;

/**
 * @author Adam
 */
public class EditUserDialogFragment extends DialogFragment {
    public static final String USER_ID = "USER_ID";
    private User mUser;

    public static EditUserDialogFragment newInstance(User user) {
        EditUserDialogFragment fragment = new EditUserDialogFragment();
        Bundle b = new Bundle();
        b.putLong(USER_ID, user.getId());
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getArguments();
        mUser = ((FRCKrawler) getActivity().getApplication()).getDBSession().getDaoSession().getUserDao().load(b.getLong(USER_ID));
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final EditText editText = new EditText(getActivity());
        editText.setHint("User Name");
        editText.setText(mUser.getName());
        builder.setView(editText);
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mUser.setName(editText.getText().toString());
                ((FRCKrawler) getActivity().getApplication()).getDBSession().getDaoSession().getUserDao().update(mUser);
                ((ListUpdateListener) getParentFragment()).updateList();
                dismiss();
            }
        });

        builder.setTitle("Edit User");
        builder.setNegativeButton("Cancel", null);
        return builder.create();
    }
}
