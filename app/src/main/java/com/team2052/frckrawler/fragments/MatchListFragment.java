package com.team2052.frckrawler.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activities.DatabaseActivity;
import com.team2052.frckrawler.binding.ListViewBinder;
import com.team2052.frckrawler.db.Match;
import com.team2052.frckrawler.fragments.dialog.UpdateMatchesProcessDialog;
import com.team2052.frckrawler.subscribers.MatchListSubscriber;
import com.team2052.frckrawler.util.Util;

import java.util.List;

import rx.Observable;

/**
 * @author Adam
 */
public class MatchListFragment extends ListViewFragment<List<Match>, MatchListSubscriber> {

    private long mEvent_id;

    public static MatchListFragment newInstance(long event_id) {
        MatchListFragment fragment = new MatchListFragment();
        Bundle b = new Bundle();
        b.putLong(DatabaseActivity.PARENT_ID, event_id);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.match_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.update_schedule) {
            UpdateMatchesProcessDialog.newInstance(mEvent_id).show(getChildFragmentManager(), "matchUpdateDialog");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mListView.setClipToPadding(false);
        mListView.setPadding(0, Util.getPixelsFromDp(getActivity(), 4), 0, 0);
        mListView.setDivider(null);
        mListView.setFocusable(false);
        mListView.setFocusableInTouchMode(false);

        setHasOptionsMenu(true);
        mEvent_id = getArguments().getLong(DatabaseActivity.PARENT_ID);
    }

    @Override
    public void inject() {
        mComponent.inject(this);
    }

    @Override
    protected Observable<? extends List<Match>> getObservable() {
        return dbManager.matchesAtEvent(mEvent_id);
    }

    @Override
    protected ListViewBinder.ListViewNoDataParams getNoDataParams() {
        return new ListViewBinder.ListViewNoDataParams("No matches found", R.drawable.ic_schedule_black_24dp);
    }
}
