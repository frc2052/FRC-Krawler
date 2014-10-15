package com.team2052.frckrawler.fragment;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.team2052.frckrawler.FRCKrawler;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.bluetooth.Server;
import com.team2052.frckrawler.database.ExportUtil;
import com.team2052.frckrawler.util.LogHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import frckrawler.Event;

public class ServerFragment extends BaseFragment implements View.OnClickListener
{
    private static final int REQUEST_BT_ENABLED = 1;
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
        v.findViewById(R.id.excel).setOnClickListener(this);
        v.findViewById(R.id.dbBackups).setOnClickListener(this);
        v.findViewById(R.id.hostToggle).setOnClickListener(this);
        ((Switch) v.findViewById(R.id.hostToggle)).setChecked(server.isOpen());
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
                new ExportToFileSystem().execute();
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
        Switch toggle = (Switch) getView().findViewById(R.id.hostToggle);
        Spinner eventChooser = (Spinner) getView().findViewById(R.id.chooseEvent);
        Event selectedEvent = mEvents.get(eventChooser.getSelectedItemPosition());
        if (null != selectedEvent) {
            if (toggle.isChecked()) {
                if (BluetoothAdapter.getDefaultAdapter() == null) {
                    Toast.makeText(getActivity(), "Sorry, your device does not " + "support Bluetooth. You will not be able to " + "open a server and receive data from scouts.", Toast.LENGTH_LONG).show();
                    ((Switch) getView().findViewById(R.id.hostToggle)).setChecked(false);
                    return;
                } else {
                    if (BluetoothAdapter.getDefaultAdapter().isEnabled())
                        server.open(selectedEvent);
                    else {
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        this.startActivityForResult(enableBtIntent, REQUEST_BT_ENABLED);
                    }
                }
            } else
                server.close();
        } else {
            Toast.makeText(getActivity(), "Could not open server. No event selected.", Toast.LENGTH_LONG).show();
            toggle.setChecked(false);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_BT_ENABLED && resultCode == Activity.RESULT_OK) {
            Spinner eventChooser = (Spinner) getView().findViewById(R.id.chooseEvent);
            Event selectedEvent = (Event) eventChooser.getSelectedItem();
            if (null != selectedEvent) {
                server.open(selectedEvent);
            }
        }
    }

    public class ExportToFileSystem extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... voids)
        {
            File fileSystem = Environment.getExternalStorageDirectory();
            Spinner eventChooser = (Spinner) getView().findViewById(R.id.chooseEvent);
            Event selectedEvent = (Event) eventChooser.getSelectedItem();
            if (fileSystem.canWrite()) {
                LogHelper.debug("Starting Export");
                File file = null;
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
            return null;
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
