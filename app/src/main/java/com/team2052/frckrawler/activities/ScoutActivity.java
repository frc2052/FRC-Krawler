package com.team2052.frckrawler.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.team2052.frckrawler.FRCKrawler;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.di.DaggerFragmentComponent;
import com.team2052.frckrawler.di.FragmentComponent;
import com.team2052.frckrawler.fragments.scout.ScoutMatchFragment;
import com.team2052.frckrawler.fragments.scout.ScoutPitFragment;
import com.team2052.frckrawler.subscribers.SubscriberModule;

import butterknife.ButterKnife;

/**
 * Created by adam on 5/2/15.
 */
public class ScoutActivity extends AppCompatActivity implements HasComponent {

    public static final int MATCH_SCOUT_TYPE = 0;
    public static final int PIT_SCOUT_TYPE = 1;
    public static final int PRACTICE_MATCH_SCOUT_TYPE = 2;
    private static final String SCOUT_TYPE_EXTRA = "com.team2052.frckrawler.SCOUT_TYPE_EXTRA";
    private static final String EVENT_ID_EXTRA = "com.team2052.frckrawler.EVENT_ID_EXTRA";

    private Fragment fragment;
    private DBManager dbManager;
    private FragmentComponent mComponent;

    public static Intent newInstance(Context context, Event event, int type) {
        Intent intent = new Intent(context, ScoutActivity.class);
        intent.putExtra(SCOUT_TYPE_EXTRA, type);
        intent.putExtra(EVENT_ID_EXTRA, event.getId());
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbManager = DBManager.getInstance(this);

        Event event = dbManager.getEventsTable().load(getIntent().getLongExtra(EVENT_ID_EXTRA, 0));
        setContentView(R.layout.activity_scout);
        ButterKnife.bind(this);

        final int scout_type = getIntent().getIntExtra(SCOUT_TYPE_EXTRA, 0);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (fragment == null) {
            int titleResId;
            switch (scout_type) {
                case PIT_SCOUT_TYPE:
                    titleResId = R.string.pit_scout;
                    fragment = ScoutPitFragment.newInstance(event);
                    break;
                case PRACTICE_MATCH_SCOUT_TYPE:
                    titleResId = R.string.practice_match_scout;
                    fragment = ScoutMatchFragment.newInstance(event, ScoutMatchFragment.MATCH_PRACTICE_TYPE);
                    break;
                case MATCH_SCOUT_TYPE:
                default:
                    titleResId = R.string.match_scout;
                    fragment = ScoutMatchFragment.newInstance(event, ScoutMatchFragment.MATCH_GAME_TYPE);
            }

            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(titleResId);
            }
        }

        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_holder, fragment);
        transaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public FragmentComponent getComponent() {
        if (mComponent == null) {
            FRCKrawler app = (FRCKrawler) getApplication();
            mComponent = DaggerFragmentComponent
                    .builder()
                    .fRCKrawlerModule(app.getModule())
                    .subscriberModule(new SubscriberModule(this))
                    .applicationComponent(app.getComponent())
                    .build();
        }
        return mComponent;
    }
}
