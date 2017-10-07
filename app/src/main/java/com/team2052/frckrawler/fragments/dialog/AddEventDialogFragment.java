package com.team2052.frckrawler.fragments.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activities.DatabaseActivity;
import com.team2052.frckrawler.data.RxDBManager;
import com.team2052.frckrawler.interfaces.RefreshListener;
import com.team2052.frckrawler.models.Event;
import com.team2052.frckrawler.models.Season;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Adam
 * @since 12/23/2014.
 */
public class AddEventDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {
    @BindView(R.id.name)
    EditText name;
    private Season mGame;

    public static AddEventDialogFragment newInstance(Season game) {
        AddEventDialogFragment fragment = new AddEventDialogFragment();
        Bundle b = new Bundle();
        b.putLong(DatabaseActivity.Companion.getPARENT_ID(), game.getId());
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mGame = RxDBManager.Companion.getInstance(getActivity()).getSeasonsTable().load(getArguments().getLong(DatabaseActivity.Companion.getPARENT_ID()));
    }

    @Override
    //Build the dialog
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_add_event, null);
        ButterKnife.bind(this, view);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Event");
        builder.setPositiveButton("Add", this);
        builder.setNegativeButton("Cancel", this);
        builder.setView(view);
        return builder.create();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (getParentFragment() != null && getParentFragment() instanceof RefreshListener) {
            ((RefreshListener) getParentFragment()).refresh();
        }
        super.onDismiss(dialog);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            Event event = new Event(null);
            event.setName(name.getText().toString());
            event.setSeason(mGame);
            event.setFmsid(null);
            event.setDate(new Date());
            RxDBManager.Companion.getInstance(getContext()).getEventsTable().insert(event);
        } else {
            dismiss();
        }
    }
}
