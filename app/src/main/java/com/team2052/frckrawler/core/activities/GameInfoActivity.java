package com.team2052.frckrawler.core.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.ActionMode;
import android.view.View;
import android.view.ViewGroup;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.core.fragments.EventsFragment;
import com.team2052.frckrawler.core.fragments.MetricsFragment;
import com.team2052.frckrawler.core.listeners.FABButtonListener;
import com.team2052.frckrawler.core.util.Utilities;
import com.team2052.frckrawler.db.Game;

/**
 * @author Adam
 * @since 10/15/2014
 */
public class GameInfoActivity extends ViewPagerFABActivity implements View.OnClickListener {

    private Game mGame;
    private ActionMode mCurrentActionMode;
    private GameInfoPagerAdapter mAdapter;

    public static Intent newInstance(Context context, Game game) {
        Intent intent = new Intent(context, GameInfoActivity.class);
        intent.putExtra(PARENT_ID, game.getId());
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (mCurrentActionMode != null) {
                    mCurrentActionMode.finish();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        mFab.setOnClickListener(this);
    }

    @Override
    public void onPreLoadViewPager() {
        mGame = mDaoSession.getGameDao().load(getIntent().getLongExtra(PARENT_ID, 0));
        setActionBarTitle("Game");
        setActionBarSubtitle(mGame.getName());
    }

    @Override
    public PagerAdapter setAdapter() {
        return mAdapter = new GameInfoPagerAdapter(getSupportFragmentManager());
    }

    @Override
    public void onActionModeStarted(ActionMode mode) {
        mCurrentActionMode = mode;
        super.onActionModeStarted(mode);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fab) {
            ((FABButtonListener) mAdapter.getRegisteredFragment(mPager.getCurrentItem())).onFABPressed();
        }
    }

    public class GameInfoPagerAdapter extends FragmentStatePagerAdapter {
        public String[] headers = new String[]{"Events", "Match Metrics", "Pit Metrics"};
        SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

        public GameInfoPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return EventsFragment.newInstance(mGame);
                case 1:
                    return MetricsFragment.newInstance(Utilities.MetricUtil.MetricType.MATCH_PERF_METRICS.ordinal(), mGame);
                case 2:
                    return MetricsFragment.newInstance(Utilities.MetricUtil.MetricType.ROBOT_METRICS.ordinal(), mGame);
            }
            return null;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        @Override
        public int getCount() {
            return headers.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return headers[position];
        }

        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }
    }

}