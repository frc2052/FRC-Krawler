package com.team2052.frckrawler.fragments.event;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activities.BaseActivity;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.db.Game;
import com.team2052.frckrawler.listeners.ListUpdateListener;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author Adam
 * @since 12/23/2014.
 */
public class AddEventDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {
    @InjectView(R.id.name)
    EditText name;
    private Game mGame;

    public static AddEventDialogFragment newInstance(Game game) {
        AddEventDialogFragment fragment = new AddEventDialogFragment();
        Bundle b = new Bundle();
        b.putLong(BaseActivity.PARENT_ID, game.getId());
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mGame = DBManager.getInstance(getActivity()).mGames.load(getArguments().getLong(BaseActivity.PARENT_ID));
    }

    @Override
    //Build the dialog
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_add_event, null);
        ButterKnife.inject(this, view);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Event");
        builder.setPositiveButton("Add", this);
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
            //DaoSession daoSession = ((FRCKrawler) getActivity().provideApplicationContext()).getDBManager();
            //daoSession.getEventDao().insert(new Event(null, name.getText().toString(), mGame.getId(), "Unknown", new Date(), null));
        } else {
            dismiss();
        }
    }
}
