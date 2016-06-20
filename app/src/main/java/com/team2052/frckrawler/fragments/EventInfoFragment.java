package com.team2052.frckrawler.fragments;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activities.BaseActivity;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.listeners.RefreshListener;
import com.team2052.frckrawler.subscribers.KeyValueListSubscriber;
import com.team2052.frckrawler.util.Util;

import java.util.Map;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class EventInfoFragment extends ListViewFragment<Map<String, String>, KeyValueListSubscriber> implements RefreshListener {
    public static final String EVENT_ID = "EVENT_ID";
    private Event mEvent;

    public static EventInfoFragment newInstance(long event_id) {
        Bundle args = new Bundle();
        args.putLong(EVENT_ID, event_id);
        EventInfoFragment fragment = new EventInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mEvent = dbManager.getEventsTable().load(getArguments().getLong(EVENT_ID));
    }

    @Override
    public void inject() {
        mComponent.inject(this);
    }

    @Override
    protected Observable<? extends Map<String, String>> getObservable() {
        return dbManager.eventInfo(mEvent);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.edit_delete_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete:
                buildDeleteDialog().show();
                break;
            case R.id.menu_edit:
                buildEditDialog().show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private AlertDialog buildDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Delete Event?");
        builder.setMessage("Are you sure you want to delete this event?");
        builder.setPositiveButton("Ok", (dialog, which) -> Observable.just(mEvent)
                .map(event -> {
                    dbManager.runInTx(() -> dbManager.getEventsTable().delete(event));
                    return event;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(onNext -> {
                    getActivity().finish();
                }));
        builder.setNegativeButton("Cancel", null);
        return builder.create();
    }

    private AlertDialog buildEditDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        AppCompatEditText name = new AppCompatEditText(getActivity());
        name.setText(mEvent.getName());
        int padding = Util.getPixelsFromDp(getActivity(), 16);
        name.setPadding(padding, padding, padding, padding);
        builder.setView(name);
        builder.setTitle("Edit Event");
        builder.setPositiveButton("Ok", (dialog, which) -> {
            mEvent.setName(name.getText().toString());
            mEvent.update();
            ((BaseActivity) getActivity()).setActionBarSubtitle(mEvent.getName());
        });
        builder.setNegativeButton("Cancel", null);
        return builder.create();
    }
}
