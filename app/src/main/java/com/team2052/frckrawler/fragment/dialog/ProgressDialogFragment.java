package com.team2052.frckrawler.fragment.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/**
 * @author Adam
 */
public class ProgressDialogFragment extends DialogFragment
{

    private String mMessage;

    public ProgressDialogFragment()
    {
    }

    public ProgressDialogFragment(String message)
    {
        mMessage = message;
    }

    public static void showLoadingProgress(FragmentManager fragman)
    {
        dismissLoadingProgress(fragman);
        ProgressDialogFragment loading = new ProgressDialogFragment("Loading...");
        loading.show(fragman, "loading");
    }

    public static void dismissLoadingProgress(FragmentManager fragman)
    {
        FragmentTransaction tr = fragman.beginTransaction();
        Fragment frag = fragman.findFragmentByTag("loading");
        if (frag != null) {
            tr.remove(frag);
        }
        tr.commit();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        final ProgressDialog dialog = new ProgressDialog(getActivity());

        dialog.setMessage(mMessage);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);


        return dialog;
    }
}
