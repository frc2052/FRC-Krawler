package com.team2052.frckrawler.core.fragments.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.team2052.frckrawler.core.FRCKrawler;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.core.activities.DatabaseActivity;
import com.team2052.frckrawler.db.DaoSession;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.Game;
import com.team2052.frckrawler.core.listeners.ListUpdateListener;

import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author Adam
 * @since 12/23/2014.
 */
public class AddEventDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {
    @InjectView(R.id.name)
    EditText name;

    @InjectView(R.id.location)
    EditText location;
    private Game mGame;

    public static AddEventDialogFragment newInstance(Game game) {
        AddEventDialogFragment fragment = new AddEventDialogFragment();
        Bundle b = new Bundle();
        b.putLong(DatabaseActivity.PARENT_ID, game.getId());
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mGame = ((FRCKrawler) getActivity().getApplication()).getDaoSession().getGameDao().load(getArguments().getLong(DatabaseActivity.PARENT_ID));
    }

    @Override
    //Build the dialog
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_add_event, null);
        ButterKnife.inject(this, view);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Event");
        builder.setPositiveButton("Ok", this);
        builder.setNegativeButton("Cancel", this);
        builder.setView(view);
        return builder.create();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (getParentFragment() != null && getParentFragment() instanceof ListUpdateListener) {
            ((ListUpdateListener) getParentFragment()).updateList();
        }
        super.onDismiss(dialog);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            DaoSession daoSession = ((FRCKrawler) getActivity().getApplicationContext()).getDaoSession();
            daoSession.getEventDao().insert(new Event(null, name.getText().toString(), mGame.getId(), location.getText().toString(), new Date(), null));
        } else {
            dismiss();
        }
    }
}
