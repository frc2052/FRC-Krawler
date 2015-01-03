package com.team2052.frckrawler.core.fragments;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.core.GlobalValues;
import com.team2052.frckrawler.core.database.ExportUtil;
import com.team2052.frckrawler.core.util.LogHelper;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.server.Server;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class ServerFragment extends BaseFragment implements View.OnClickListener {
    private static final int REQUEST_BT_ENABLED = 1;
    @InjectView(R.id.chooseEvent)
    Spinner eventSpinner;
    @InjectView(R.id.hostToggle)
    SwitchCompat mHostToggle;
    @InjectView(R.id.server_setting_compile_weight)
    EditText compileWeight;
    private Server server;
    private List<Event> mEvents;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.server = Server.getInstance(getActivity());
        new GetEventsTask().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_server, null);
        ButterKnife.inject(this, v);
        v.findViewById(R.id.excel).setOnClickListener(this);
        //v.findViewById(R.id.dbBackups).setOnClickListener(this);
        v.findViewById(R.id.hostToggle).setOnClickListener(this);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(GlobalValues.PREFS_FILE_NAME, 0);
        compileWeight.setText(Float.toString(sharedPreferences.getFloat(GlobalValues.PREFS_COMPILE_WEIGHT, 1.0f)));
        mHostToggle.setChecked(server.isOpen());
        return v;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.hostToggle:
                openServer();
                break;
            case R.id.excel:
                if (getSelectedEvent() != null) {
                    new ExportToFileSystem().execute();
                }
                break;
            /*case R.id.dbBackups:
                try {
                    ((FRCKrawler) getActivity().getApplication()).copyDB(new File(Environment.getExternalStorageDirectory(), "FRCKrawlerBackup-" + DateFormat.getDateFormat(getActivity()).format(new Date()) + ".db"));
                    Toast.makeText(getActivity(), "Made a backup", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
        }
    }

    private void openServer() {
        Event selectedEvent = getSelectedEvent();
        if (null != selectedEvent) {

            if (mHostToggle.isChecked()) {
                if (BluetoothAdapter.getDefaultAdapter() == null) {
                    Toast.makeText(getActivity(), "Sorry, your device does not " + "support Bluetooth. You will not be able to " + "open a server and receive data from scouts.", Toast.LENGTH_LONG).show();
                    mHostToggle.setChecked(false);
                    return;
                }

                if (BluetoothAdapter.getDefaultAdapter().isEnabled())
                    server.open(selectedEvent);
                else {
                    this.startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_BT_ENABLED);
                }

            } else {
                server.close();
            }

        } else {
            Toast.makeText(getActivity(), "Could not open server. No event selected.", Toast.LENGTH_LONG).show();
            mHostToggle.setChecked(false);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_BT_ENABLED && resultCode == Activity.RESULT_OK) {
            Event selectedEvent = getSelectedEvent();
            if (selectedEvent != null) {
                server.open(selectedEvent);
            }
        }
    }

    @Nullable
    private Event getSelectedEvent() {
        if (mEvents.size() <= 0) {
            return null;
        }
        return mEvents.get(eventSpinner.getSelectedItemPosition());
    }

    @OnClick(R.id.server_settings_restore_defaults)
    public void onRestoreButtonClicked(Button button) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(GlobalValues.PREFS_FILE_NAME, 0);
        sharedPreferences.edit().putFloat(GlobalValues.PREFS_COMPILE_WEIGHT, 1.0f).apply();
        compileWeight.setText("1.0");
    }

    @OnClick(R.id.server_settings_save)
    public void onServerSettingSaveButtonClicked(Button button) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(GlobalValues.PREFS_FILE_NAME, 0);
        sharedPreferences.edit().putFloat(GlobalValues.PREFS_COMPILE_WEIGHT, Float.parseFloat(compileWeight.getText().toString())).apply();
    }

    public class ExportToFileSystem extends AsyncTask<Void, Void, File> {
        final float compileWeight;
        File file = null;

        public ExportToFileSystem() {
            this.compileWeight = getActivity().getSharedPreferences(GlobalValues.PREFS_FILE_NAME, 0).getFloat(GlobalValues.PREFS_COMPILE_WEIGHT, 1.0f);
        }

        @Override
        protected File doInBackground(Void... voids) {
            File fileSystem = Environment.getExternalStorageDirectory();
            Event selectedEvent = getSelectedEvent();

            if (selectedEvent != null) {
                if (fileSystem.canWrite()) {
                    LogHelper.debug("Starting Export");
                    try {
                        file = File.createTempFile(
                                selectedEvent.getGame().getName() + "_" + selectedEvent.getName() + "_" + "Summary",  /* prefix */
                                ".csv",         /* suffix */
                                fileSystem      /* directory */
                        );
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (file != null) {
                        return ExportUtil.exportEventDataToCSV(selectedEvent, file, mDaoSession, compileWeight);
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(File file) {
            if (file != null) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                shareIntent.setType("file/csv");
                startActivity(Intent.createChooser(shareIntent, "Send To"));

            }
        }
    }

    private class GetEventsTask extends AsyncTask<Void, Void, List<Event>> {

        @Override
        protected List<Event> doInBackground(Void... params) {
            return mDaoSession.getEventDao().loadAll();
        }

        @Override
        protected void onPostExecute(List<Event> _events) {
            Spinner eventChooser = (Spinner) getView().findViewById(R.id.chooseEvent);
            mEvents = _events;
            List<String> eventNames = new ArrayList<>();
            for (Event event : _events) {
                eventNames.add(event.getGame().getName() + ", " + event.getName());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, eventNames);
            eventChooser.setAdapter(adapter);
        }
    }
}
