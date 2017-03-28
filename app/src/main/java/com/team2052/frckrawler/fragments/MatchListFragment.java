package com.team2052.frckrawler.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activities.DatabaseActivity;
import com.team2052.frckrawler.binding.ListViewNoDataParams;
import com.team2052.frckrawler.binding.RecyclerViewBinder;
import com.team2052.frckrawler.db.Match;
import com.team2052.frckrawler.fragments.dialog.UpdateMatchesProcessDialog;
import com.team2052.frckrawler.listitems.smart.MatchItemView;

import java.util.List;

import io.nlopez.smartadapters.SmartAdapter;
import rx.Observable;

/**
 * @author Adam
 */
public class MatchListFragment extends RecyclerViewFragment<List<Match>, RecyclerViewBinder> {

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
        setHasOptionsMenu(true);
        mEvent_id = getArguments().getLong(DatabaseActivity.PARENT_ID);
    }

    @Override
    protected boolean showDividers() {
        return false;
    }

    @Override
    public void inject() {
        mComponent.inject(this);
    }

    @Override
    protected Observable<? extends List<Match>> getObservable() {
        return rxDbManager.matchesAtEvent(mEvent_id);
    }

    @Override
    protected ListViewNoDataParams getNoDataParams() {
        return new ListViewNoDataParams("No matches found", R.drawable.ic_schedule_black_24dp);
    }

    @Override
    public void provideAdapterCreator(SmartAdapter.MultiAdaptersCreator creator) {
        creator.map(Match.class, MatchItemView.class);
    }
}
