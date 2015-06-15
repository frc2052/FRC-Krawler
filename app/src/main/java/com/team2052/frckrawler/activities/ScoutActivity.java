package com.team2052.frckrawler.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.fragments.scout.ScoutMatchFragment;
import com.team2052.frckrawler.fragments.scout.ScoutPitFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by adam on 5/2/15.
 */
public class ScoutActivity extends BaseActivity {

    public static final int MATCH_SCOUT_TYPE = 0;
    public static final int PIT_SCOUT_TYPE = 1;
    public static final int PRACTICE_MATCH_SCOUT_TYPE = 2;
    private static final String SCOUT_TYPE_EXTRA = "com.team2052.frckrawler.SCOUT_TYPE_EXTRA";
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    private Fragment fragment;

    public static Intent newInstance(Context context, int type) {
        Intent intent = new Intent(context, ScoutActivity.class);
        intent.putExtra(SCOUT_TYPE_EXTRA, type);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scout_main);
        ButterKnife.inject(this);

        setSupportActionBar(toolbar);

        final int scout_type = getIntent().getIntExtra(SCOUT_TYPE_EXTRA, 0);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (fragment == null) {
            int titleResId;
            switch (scout_type) {
                case PIT_SCOUT_TYPE:
                    titleResId = R.string.pit_scout;
                    fragment = new ScoutPitFragment();
                    break;
                case PRACTICE_MATCH_SCOUT_TYPE:
                    titleResId = R.string.practice_match_scout;
                    fragment = ScoutMatchFragment.newInstance(ScoutMatchFragment.MATCH_PRACTICE_TYPE);
                    break;
                case MATCH_SCOUT_TYPE:
                default:
                    titleResId = R.string.match_scout;
                    fragment = ScoutMatchFragment.newInstance(ScoutMatchFragment.MATCH_GAME_TYPE);
            }

            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(titleResId);
            }
        }


        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_holder, fragment);
        transaction.commit();
    }
}
