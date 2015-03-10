package com.team2052.frckrawler.core.fragments.dialog.process;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * @author Adam
 * @since 3/9/2015.
 */
public class BaseProgressDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage(getMessage());
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        return dialog;
    }

    public CharSequence getMessage() {
        return "Loading";
    }
}
