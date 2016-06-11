package com.team2052.frckrawler.activities;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.fragments.dialog.ExportDialogFragment;
import com.team2052.frckrawler.fragments.dialog.PickEventDialogFragment;

public class SettingsActivity extends AppCompatActivity implements PickEventDialogFragment.EventPickedListener {
    private static String EXPORT_PREFERENCE_KEY = "compile_export_preference";

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

    public static class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

        private static final String TAG = SettingsFragment.class.getSimpleName();

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);


            addPreferencesFromResource(R.xml.preferences);
            int numEvents = DBManager.getInstance(getActivity()).getEventsTable().getAllEvents().size();

            if(numEvents < 1){
                findPreference(EXPORT_PREFERENCE_KEY).setEnabled(false);
            }

            findPreference(EXPORT_PREFERENCE_KEY).setOnPreferenceClickListener(this);
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            String key = preference.getKey();
            if (key.equals(EXPORT_PREFERENCE_KEY)) {
                new PickEventDialogFragment().show(((AppCompatActivity) getActivity()).getSupportFragmentManager(), "pickEventDialog");
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
