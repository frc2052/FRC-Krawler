package com.team2052.frckrawler.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ActionMode;

import com.team2052.frckrawler.fragment.game.EventsFragment;
import com.team2052.frckrawler.fragment.game.MetricsFragment;

import frckrawler.Game;

/**
 * @author Adam
 * @since 10/15/2014
 */
public class GameInfoActivity extends ViewPagerActivity
{

    private Game mGame;
    private ActionMode mCurrentActionMode;

    public static Intent newInstance(Context context, Game game)
    {
        Intent intent = new Intent(context, GameInfoActivity.class);
        intent.putExtra(PARENT_ID, game.getId());
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mTabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {
            }

            @Override
            public void onPageSelected(int position)
            {
                if (mCurrentActionMode != null) {
                    mCurrentActionMode.finish();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {
            }
        });
    }

    @Override
    public PagerAdapter setAdapter()
    {
        return new GameInfoPagerAdapter(getSupportFragmentManager());
    }

    @Override
    public void onPreLoadViewPager()
    {
        mGame = mDaoSession.getGameDao().load(getIntent().getLongExtra(PARENT_ID, 0));
        setActionBarTitle("Game");
        setActionBarSubtitle(mGame.getName());
    }

    public class GameInfoPagerAdapter extends FragmentPagerAdapter
    {
        public String[] headers = new String[]{"Events", "Match Metrics", "Pit Metrics"};

        public GameInfoPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            switch (position) {
                case 0:
                    return EventsFragment.newInstance(mGame);
                case 1:
                    return MetricsFragment.newInstance(MetricsActivity.MetricType.MATCH_PERF_METRICS.ordinal(), mGame);
                case 2:
                    return MetricsFragment.newInstance(MetricsActivity.MetricType.ROBOT_METRICS.ordinal(), mGame);
            }
            return null;
        }

        @Override
        public int getCount()
        {
            return headers.length;
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            return headers[position];
        }
    }

    @Override
    public void onActionModeStarted(ActionMode mode)
    {
        mCurrentActionMode = mode;
        super.onActionModeStarted(mode);
    }
}
