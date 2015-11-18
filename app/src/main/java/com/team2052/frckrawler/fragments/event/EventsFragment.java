package com.team2052.frckrawler.fragments.event;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activities.BaseActivity;
import com.team2052.frckrawler.activities.EventInfoActivity;
import com.team2052.frckrawler.database.subscribers.EventListSubscriber;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.fragments.ListViewFragment;
import com.team2052.frckrawler.fragments.event.dialog.ImportDataSimpleDialogFragment;
import com.team2052.frckrawler.listeners.FABButtonListener;
import com.team2052.frckrawler.listitems.ListElement;
import com.team2052.frckrawler.tba.ConnectionChecker;

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
        bundle.putLong(BaseActivity.PARENT_ID, game_id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void inject() {
        mComponent.inject(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mGame_id = getArguments().getLong(BaseActivity.PARENT_ID, 0);
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
        if (ConnectionChecker.isConnectedToInternet(getActivity()))
            ImportDataSimpleDialogFragment.newInstance(mGame_id).show(getChildFragmentManager(), "importEvent");
        else
            Snackbar.make(getView(), getActivity().getResources().getString(R.string.not_connected_to_internet), Snackbar.LENGTH_LONG).show();
    }
}
