package com.team2052.frckrawler.fragments.team;

import android.os.Bundle;

import com.team2052.frckrawler.activities.BaseActivity;
import com.team2052.frckrawler.db.Robot;
import com.team2052.frckrawler.fragments.BaseFragment;

/**
 * @author Adam
 * @since 10/31/2014
 */
public class TeamInfoFragment extends BaseFragment {
    public static TeamInfoFragment newInstance(Robot robot) {
        TeamInfoFragment fragment = new TeamInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(BaseActivity.PARENT_ID, robot.getId());
        fragment.setArguments(bundle);
        return fragment;
    }

}
