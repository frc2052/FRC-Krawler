package com.team2052.frckrawler.activities;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.databinding.LayoutTabFabBinding;
import com.team2052.frckrawler.db.Team;
import com.team2052.frckrawler.fragments.team.RobotsFragment;
import com.team2052.frckrawler.fragments.team.TeamInfoFragment;

/**
 * @author Adam
 */
public class TeamInfoActivity extends BaseActivity {
    private Team mTeam;
    private LayoutTabFabBinding binding;

    public static Intent newInstance(Context context, Team team) {
        Intent intent = new Intent(context, TeamInfoActivity.class);
        intent.putExtra(PARENT_ID, team.getNumber());
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.layout_tab_fab);
        mTeam = mDbManager.getTeamsTable().load(getIntent().getLongExtra(PARENT_ID, 0));
        setSupportActionBar(binding.toolbar);

        binding.viewPager.setAdapter(new ViewTeamPagerAdapter(getSupportFragmentManager()));
        binding.tabLayout.setupWithViewPager(binding.viewPager);

        setActionBarTitle(getString(R.string.team));
        setActionBarSubtitle(String.valueOf(mTeam.getNumber()));
    }

    public class ViewTeamPagerAdapter extends FragmentPagerAdapter {
        public final String[] headers = getResources().getStringArray(R.array.team_tab_titles);

        public ViewTeamPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = TeamInfoFragment.newInstance(mTeam);
                    break;
                case 1:
                    fragment = RobotsFragment.newInstance(mTeam);
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return headers.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return headers[position];
        }
    }
}
