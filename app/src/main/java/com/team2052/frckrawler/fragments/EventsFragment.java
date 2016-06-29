package com.team2052.frckrawler.fragments;

import android.os.Bundle;
import android.view.View;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activities.DatabaseActivity;
import com.team2052.frckrawler.activities.EventInfoActivity;
import com.team2052.frckrawler.binding.ListViewBinder;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.fragments.dialog.ImportDataSimpleDialogFragment;
import com.team2052.frckrawler.listeners.FABButtonListener;
import com.team2052.frckrawler.listitems.ListElement;
import com.team2052.frckrawler.subscribers.EventListSubscriber;

import java.util.List;

import rx.Observable;

/**
 * Created by Adam on 11/17/2015.
 */
public class EventsFragment extends ListViewFragment<List<Event>, EventListSubscriber> implements FABButtonListener {
    private long mGame_id;

    public static EventsFragment newInstance(long game_id) {
        EventsFragment fragment = new EventsFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(DatabaseActivity.PARENT_ID, game_id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void inject() {
        mComponent.inject(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mGame_id = getArguments().getLong(DatabaseActivity.PARENT_ID, 0);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListView.setOnItemClickListener((parent, view1, position, id) -> {
            ListElement listElement = (ListElement) parent.getAdapter().getItem(position);
            startActivity(EventInfoActivity.newInstance(getActivity(), Long.valueOf(listElement.getKey())));
        });
    }

    @Override
    protected Observable<? extends List<Event>> getObservable() {
        return dbManager.eventsByGame(mGame_id);
    }

    @Override
    public void onFABPressed() {
        ImportDataSimpleDialogFragment.newInstance(mGame_id).show(getChildFragmentManager(), "importEvent");
    }

    @Override
    protected ListViewBinder.ListViewNoDataParams getNoDataParams() {
        return new ListViewBinder.ListViewNoDataParams("No events found", R.drawable.ic_event);
    }
}
