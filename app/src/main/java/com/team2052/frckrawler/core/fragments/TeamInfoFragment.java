package com.team2052.frckrawler.core.fragments;

import android.os.Bundle;

import com.team2052.frckrawler.core.activities.DatabaseActivity;
import com.team2052.frckrawler.db.Robot;

/**
 * @author Adam
 * @since 10/31/2014
 */
public class TeamInfoFragment extends BaseFragment {
    public static TeamInfoFragment newInstance(Robot robot) {
        TeamInfoFragment fragment = new TeamInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(DatabaseActivity.PARENT_ID, robot.getId());
        fragment.setArguments(bundle);
        return fragment;
    }

}
