package com.team2052.frckrawler.fragments;

import android.os.Bundle;

import com.team2052.frckrawler.activities.DatabaseActivity;
import com.team2052.frckrawler.subscribers.KeyValueListSubscriber;

import java.util.Map;

import rx.Observable;

/**
 * @author Adam
 * @since 10/31/2014
 */
public class TeamInfoFragment extends ListViewFragment<Map<String, String>, KeyValueListSubscriber> {
    public static TeamInfoFragment newInstance(long team_id) {
        TeamInfoFragment fragment = new TeamInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(DatabaseActivity.PARENT_ID, team_id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void inject() {
        mComponent.inject(this);
    }

    @Override
    protected Observable<? extends Map<String, String>> getObservable() {
        return dbManager.teamInfo(getArguments().getLong(DatabaseActivity.PARENT_ID));
    }
}
