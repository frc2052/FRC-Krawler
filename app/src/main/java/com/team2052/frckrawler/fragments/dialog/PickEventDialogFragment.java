package com.team2052.frckrawler.fragments.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.common.collect.Lists;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.db.Event;

import java.util.List;

/**
 * Created by Adam on 5/29/2016.
 */
public class PickEventDialogFragment extends DialogFragment {
    private List<Event> events = Lists.newArrayList();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(!(getActivity() instanceof EventPickedListener)){
            dismiss();
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Spinner spinner = new Spinner(getActivity());
        List<String> eventStrings = Lists.newArrayList();
        for (Event event : events) {
            eventStrings.add(event.getGame().getName() + ", " + event.getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, eventStrings);
        spinner.setAdapter(adapter);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Pick Event");
        builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton("Ok", (dialog, which) -> {
            Event event = events.get(spinner.getSelectedItemPosition());
            ((EventPickedListener) getActivity()).pickedEvent(event);
        });
        builder.setView(spinner);
        return builder.create();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        events = DBManager.getInstance(getActivity()).getEventsTable().getAllEvents();
        super.onCreate(savedInstanceState);
    }

    public interface EventPickedListener {
        void pickedEvent(Event event);
    }
}
