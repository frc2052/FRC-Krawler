package com.team2052.frckrawler.activities;

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
    private static String EXPORT_PREFERENCE_KEY = "compile_export_preference";
    private FragmentComponent mComponent;

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
        ExportDialogFragment.newInstance(event).show(getSupportFragmentManager(), "exportDialogFragment");
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

    public static class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            int numEvents = DBManager.getInstance(getActivity()).getEventsTable().getAllEvents().size();

            if (numEvents < 1) {
                findPreference(EXPORT_PREFERENCE_KEY).setEnabled(false);
            }

            findPreference(EXPORT_PREFERENCE_KEY).setOnPreferenceClickListener(this);
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            String key = preference.getKey();
            if (key.equals(EXPORT_PREFERENCE_KEY)) {
                new PickEventDialogFragment().show(((AppCompatActivity) getActivity()).getSupportFragmentManager(), "pickEventDialog");
            } else {

            }
            return false;
        }
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
}
