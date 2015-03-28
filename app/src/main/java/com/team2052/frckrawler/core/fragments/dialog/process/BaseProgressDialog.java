package com.team2052.frckrawler.core.fragments.dialog.process;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.team2052.frckrawler.core.FRCKrawler;
import com.team2052.frckrawler.core.database.DBManager;

/**
 * @author Adam
 * @since 3/9/2015.
 */
public class BaseProgressDialog extends DialogFragment {

    protected DBManager mDbManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mDbManager = ((FRCKrawler) getActivity().getApplication()).getDBSession();
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage(getMessage());
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        setCancelable(false);
        return dialog;
    }

    public CharSequence getMessage() {
        return "Loading";
    }
}