package com.team2052.frckrawler.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.fragments.scout.ScoutMatchFragment;
import com.team2052.frckrawler.fragments.scout.ScoutPitFragment;
import com.team2052.frckrawler.helpers.metric.MetricHelper;
import com.team2052.frckrawler.interfaces.HasComponent;
import com.team2052.frckrawler.models.Event;

import butterknife.ButterKnife;

public class ScoutActivity extends DatabaseActivity implements HasComponent {

    public static final int MATCH_SCOUT_TYPE = 0;
    public static final int PIT_SCOUT_TYPE = 1;
    public static final int PRACTICE_MATCH_SCOUT_TYPE = 2;
    private static final String SCOUT_TYPE_EXTRA = "com.team2052.frckrawler.SCOUT_TYPE_EXTRA";
    private static final String EVENT_ID_EXTRA = "com.team2052.frckrawler.EVENT_ID_EXTRA";

    private Fragment fragment;

    public static Intent newInstance(Context context, Event event, int type) {
        Intent intent = new Intent(context, ScoutActivity.class);
        intent.putExtra(SCOUT_TYPE_EXTRA, type);
        intent.putExtra(EVENT_ID_EXTRA, event.getId());
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setNavigationDrawerEnabled(false);
        Event event = rxDbManager.getEventsTable().load(getIntent().getLongExtra(EVENT_ID_EXTRA, 0));

        if (event == null) {
            finish();
            return;
        }

        setContentView(R.layout.activity_scout);
        ButterKnife.bind(this);

        final int scout_type = getIntent().getIntExtra(SCOUT_TYPE_EXTRA, 0);

        if (fragment == null) {
            switch (scout_type) {
                case PIT_SCOUT_TYPE:
                    fragment = ScoutPitFragment.newInstance(event);
                    break;
                case PRACTICE_MATCH_SCOUT_TYPE:
                    fragment = ScoutMatchFragment.newInstance(event, MetricHelper.MATCH_PRACTICE_TYPE);
                    break;
                case MATCH_SCOUT_TYPE:
                default:
                    fragment = ScoutMatchFragment.newInstance(event, MetricHelper.MATCH_GAME_TYPE);
            }
        }

        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_holder, fragment);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Leave Scouting?");
        alertDialog.setMessage("Are you sure you want to leave scouting? All unsaved data will be lost.");
        alertDialog.setPositiveButton("I'm sure", (dialog, which) -> super.onBackPressed());
        alertDialog.setNegativeButton("No, I don't", null);
        alertDialog.create().show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void inject() {
        getComponent().inject(this);
    }
}
