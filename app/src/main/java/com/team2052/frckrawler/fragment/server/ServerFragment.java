package com.team2052.frckrawler.fragment.server;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.bluetooth.Server;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.models.Event;

import java.util.List;

public class ServerFragment extends Fragment implements View.OnClickListener {
    private static final int REQUEST_BT_ENABLED = 1;
    private Server server;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.server = Server.getInstance(getActivity());
        new GetEventsTask().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_bluetooth_server_manager, null);
        v.findViewById(R.id.excel).setOnClickListener(this);
        v.findViewById(R.id.dbBackups).setOnClickListener(this);
        v.findViewById(R.id.hostToggle).setOnClickListener(this);
        ((Switch) v.findViewById(R.id.hostToggle)).setChecked(server.isOpen());
        return v;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.hostToggle:
                Switch toggle = (Switch) getView().findViewById(R.id.hostToggle);
                Spinner eventChooser = (Spinner) getView().findViewById(R.id.chooseEvent);
                Event selectedEvent = (Event) eventChooser.getSelectedItem();
                if (null != selectedEvent) {
                    if (toggle.isChecked()) {
                        if (BluetoothAdapter.getDefaultAdapter() == null) {
                            Toast.makeText(getActivity(), "Sorry, your device does not " + "support Bluetooth. You will not be able to " + "open a server and recieve data from scouts.", Toast.LENGTH_LONG).show();
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
                break;
            case R.id.excel:
                /*Intent excelIntent = new Intent(getActivity(), ExcelExportDialogActivity.class);
                startActivity(excelIntent);*/
                break;
            case R.id.dbBackups:
                /*Intent dbIntent = new Intent(getActivity(), DBBackupDialogActivity.class);
                startActivity(dbIntent);*/
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_BT_ENABLED && resultCode == getActivity().RESULT_OK) {
            Spinner eventChooser = (Spinner) getView().findViewById(R.id.chooseEvent);
            Event selectedEvent = (Event) eventChooser.getSelectedItem();
            if (null != selectedEvent) {
                server.open(selectedEvent);
            }
        }
    }

    private class GetEventsTask extends AsyncTask<Void, Void, List<Event>> {

        @Override
        protected List<Event> doInBackground(Void... params) {
            return DBManager.loadAllEvents();
        }

        @Override
        protected void onPostExecute(List<Event> _events) {
            Spinner eventChooser = (Spinner) getView().findViewById(R.id.chooseEvent);
            ArrayAdapter<Event> adapter = new ArrayAdapter<Event>(getActivity(), android.R.layout.simple_list_item_1, _events);
            eventChooser.setAdapter(adapter);
        }
    }
}
