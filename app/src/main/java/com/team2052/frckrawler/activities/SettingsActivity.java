package com.team2052.frckrawler.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.team2052.frckrawler.FRCKrawler;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.di.DaggerFragmentComponent;
import com.team2052.frckrawler.di.FragmentComponent;
import com.team2052.frckrawler.fragments.dialog.ExportDialogFragment;
import com.team2052.frckrawler.fragments.dialog.PickEventDialogFragment;
import com.team2052.frckrawler.subscribers.SubscriberModule;

public class SettingsActivity extends AppCompatActivity implements PickEventDialogFragment.EventPickedListener, HasComponent {
    private static final String EXPORT_PREFERENCE_KEY = "compile_export_preference";
    private static final String EXPORT_RAW_PREFERENCE_KEY = "compile_raw_export";
    private FragmentComponent mComponent;

    private int clickedPreference = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    @Override
    public void pickedEvent(Event event) {
        ExportDialogFragment.newInstance(event, clickedPreference).show(getSupportFragmentManager(), "exportDialogFragment");
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            int numEvents = DBManager.getInstance(getActivity()).getEventsTable().getAllEvents().size();

            findPreference(EXPORT_PREFERENCE_KEY).setEnabled(numEvents >= 1);
            findPreference(EXPORT_RAW_PREFERENCE_KEY).setEnabled(numEvents >= 1);

            Preference teamWebsite = findPreference("team_website");
            teamWebsite.setIntent(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.team2052.com/")));

            Preference githubLink = findPreference("github_link");
            githubLink.setIntent(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/frc2052/FRC-Krawler")));

            Preference learnMoreLink = findPreference("learn_more_link");
            learnMoreLink.setIntent(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.team2052.com/frckrawler/")));

            findPreference(EXPORT_PREFERENCE_KEY).setOnPreferenceClickListener(this);
            findPreference(EXPORT_RAW_PREFERENCE_KEY).setOnPreferenceClickListener(this);
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            String key = preference.getKey();
            if (key.equals(EXPORT_PREFERENCE_KEY) || key.equals(EXPORT_RAW_PREFERENCE_KEY)) {
                new PickEventDialogFragment().show(((AppCompatActivity) getActivity()).getSupportFragmentManager(), "pickEventDialog");
            }

            switch (key) {
                case EXPORT_PREFERENCE_KEY:
                    clickedPreference = 0;
                    break;
                case EXPORT_RAW_PREFERENCE_KEY:
                    clickedPreference = 1;
                    break;
            }
            return false;
        }
    }
}
