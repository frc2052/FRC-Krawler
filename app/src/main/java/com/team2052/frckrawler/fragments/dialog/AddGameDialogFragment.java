package com.team2052.frckrawler.fragments.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.db.Game;
import com.team2052.frckrawler.listeners.RefreshListener;

/**
 * @author Adam
 */
public class AddGameDialogFragment extends DialogFragment {
    private RefreshListener listener;
    private DBManager mDbSession;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listener = (RefreshListener) getParentFragment();
        mDbSession = DBManager.getInstance(getActivity());
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_dialog_add_game, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppAlertDialogStyle);

        builder.setPositiveButton("Add", (dialog, which) -> {
            Game game = new Game(null, ((TextView) getDialog().getWindow().findViewById(R.id.nameVal)).getText().toString());
            mDbSession.getGamesTable().insert(game);
            listener.refresh();
            dismiss();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dismiss();
        });

        builder.setView(view);
        builder.setTitle("Add Game");
        return builder.create();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        listener.refresh();
        super.onDismiss(dialog);
    }
}
