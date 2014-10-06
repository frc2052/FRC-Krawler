package com.team2052.frckrawler.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.TextView;

import com.team2052.frckrawler.FRCKrawler;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.listeners.ListUpdateListener;

import frckrawler.Game;

/**
 * @author Adam
 */
public class AddGameDialogFragment extends DialogFragment
{
    private ListUpdateListener listener;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        listener = (ListUpdateListener) getParentFragment();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_dialog_add_game, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Game game = new Game(null, ((TextView) getDialog().getWindow().findViewById(R.id.nameVal)).getText().toString());
                ((FRCKrawler)getActivity().getApplication()).getDaoSession().getGameDao().insert(game);
                listener.updateList();
                dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dismiss();
            }
        });
        builder.setView(view);
        builder.setTitle("Add Game");
        return builder.create();
    }

    @Override
    public void onDismiss(DialogInterface dialog)
    {
        listener.updateList();
        super.onDismiss(dialog);
    }
}
