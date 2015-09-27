package com.team2052.frckrawler.activities;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.adapters.InstanceFragmentStatePagerAdapter;
import com.team2052.frckrawler.database.MetricHelper;
import com.team2052.frckrawler.databinding.LayoutTabFabBinding;
import com.team2052.frckrawler.db.Game;
import com.team2052.frckrawler.fragments.event.EventsFragment;
import com.team2052.frckrawler.fragments.game.GameInfoFragment;
import com.team2052.frckrawler.fragments.game.MetricsGameFragment;
import com.team2052.frckrawler.listeners.FABButtonListener;
import com.team2052.frckrawler.listeners.ListUpdateListener;

/**
 * @author Adam
 * @since 10/15/2014
 */
public class GameInfoActivity extends BaseActivity implements View.OnClickListener {

    private Game mGame;
    private GameInfoPagerAdapter mAdapter;
    private LayoutTabFabBinding binding;

    public static Intent newInstance(Context context, Game game) {
        Intent intent = new Intent(context, GameInfoActivity.class);
        intent.putExtra(PARENT_ID, game.getId());
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.layout_tab_fab);

        mGame = mDbManager.getGamesTable().load(getIntent().getLongExtra(PARENT_ID, 0));
        if (mGame == null) {
            finish();
        }

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setActionBarTitle("Game");
        setActionBarSubtitle(mGame.getName());

        mAdapter = new GameInfoPagerAdapter(getSupportFragmentManager());
        binding.viewPager.setAdapter(mAdapter);
        binding.tabLayout.setupWithViewPager(binding.viewPager);

        binding.floatingActionButton.setOnClickListener(this);

        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    binding.floatingActionButton.hide();
                    ((ListUpdateListener) mAdapter.getRegisteredFragment(0)).updateList();
                } else {
                    binding.floatingActionButton.show();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(HomeActivity.newInstance(this, R.id.nav_item_games).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.floating_action_button) {
            ((FABButtonListener) mAdapter.getRegisteredFragment(binding.viewPager.getCurrentItem())).onFABPressed();
        }
    }

    public class GameInfoPagerAdapter extends InstanceFragmentStatePagerAdapter {
        public String[] headers = new String[]{"Info", "Events", "Match Metrics", "Pit Metrics"};

        public GameInfoPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return GameInfoFragment.newInstance(mGame);
                case 1:
                    return EventsFragment.newInstance(mGame);
                case 2:
                    return MetricsGameFragment.newInstance(MetricHelper.MetricCategory.MATCH_PERF_METRICS.id, mGame);
                case 3:
                    return MetricsGameFragment.newInstance(MetricHelper.MetricCategory.ROBOT_METRICS.id, mGame);
            }
            return null;
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
