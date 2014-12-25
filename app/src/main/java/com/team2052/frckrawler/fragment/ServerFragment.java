package com.team2052.frckrawler.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.team2052.frckrawler.FRCKrawler;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.bluetooth.server.Server;
import com.team2052.frckrawler.database.ExportUtil;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.util.LogHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ServerFragment extends BaseFragment implements View.OnClickListener
{
    private static final int REQUEST_BT_ENABLED = 1;
    @InjectView(R.id.chooseEvent)
    Spinner eventSpinner;
    @InjectView(R.id.hostToggle)
    SwitchCompat mHostToggle;
    private Server server;
    private List<Event> mEvents;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.server = Server.getInstance(getActivity());
        new GetEventsTask().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.activity_bluetooth_server_manager, null);
        ButterKnife.inject(this, v);
        v.findViewById(R.id.excel).setOnClickListener(this);
        v.findViewById(R.id.dbBackups).setOnClickListener(this);
        v.findViewById(R.id.hostToggle).setOnClickListener(this);
        mHostToggle.setChecked(server.isOpen());
        return v;
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId()) {
            case R.id.hostToggle:
                openServer();
                break;
            case R.id.excel:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Export to File System?");
                builder.setNegativeButton("Cancel", null);
                builder.setPositiveButton("Export", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        new ExportToFileSystem().execute();
                    }
                });
                builder.create().show();
                break;
            case R.id.dbBackups:
                try {
                    ((FRCKrawler) getActivity().getApplication()).copyDB(new File(Environment.getExternalStorageDirectory(), "FRCKrawlerBackup-" + DateFormat.getDateFormat(getActivity()).format(new Date()) + ".db"));
                    Toast.makeText(getActivity(), "Made a backup", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
    }

    private void openServer()
    {
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
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_BT_ENABLED && resultCode == Activity.RESULT_OK) {
            Event selectedEvent = getSelectedEvent();
            if (selectedEvent != null) {
                server.open(selectedEvent);
            }
        }
    }

    @Nullable
    private Event getSelectedEvent()
    {
        if (mEvents.size() <= 0) {
            return null;
        }
        return mEvents.get(eventSpinner.getSelectedItemPosition());
    }

    public class ExportToFileSystem extends AsyncTask<Void, Void, Void>
    {
        File file = null;

        @Override
        protected Void doInBackground(Void... voids)
        {
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
                        ExportUtil.exportEventDataToCSV(selectedEvent, file, mDaoSession);
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Export Finished");
            builder.setMessage("Exported as " + file.getName());
            builder.create().show();
        }
    }

    private class GetEventsTask extends AsyncTask<Void, Void, List<Event>>
    {

        @Override
        protected List<Event> doInBackground(Void... params)
        {
            return mDaoSession.getEventDao().loadAll();
        }

        @Override
        protected void onPostExecute(List<Event> _events)
        {
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
