package com.team2052.frckrawler;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.team2052.frckrawler.activity.dialog.DBBackupDialogActivity;
import com.team2052.frckrawler.activity.dialog.ExcelExportDialogActivity;
import com.team2052.frckrawler.activity.TabActivity;
import com.team2052.frckrawler.bluetooth.Server;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.Event;

public class ServerActivity extends TabActivity implements View.OnClickListener {

    private static final int REQUEST_BT_ENABLED = 1;
    private DBManager dbManager;
    private Server server;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_server_manager);
        findViewById(R.id.excel).setOnClickListener(this);
        findViewById(R.id.dbBackups).setOnClickListener(this);
        findViewById(R.id.hostToggle).setOnClickListener(this);
        dbManager = DBManager.getInstance(this);
        server = Server.getInstance(this);
        new GetEventsTask().execute();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((ToggleButton) findViewById(R.id.hostToggle)).
                setChecked(server.isOpen());
        if (server.isOpen()) {
            //TODO Set spinner to selected event
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.hostToggle:
                ToggleButton toggle = (ToggleButton) findViewById(R.id.hostToggle);
                Spinner eventChooser = (Spinner) findViewById(R.id.chooseEvent);
                Event selectedEvent = (Event) eventChooser.getSelectedItem();
                if (null != selectedEvent) {
                    if (toggle.isChecked()) {
                        if (BluetoothAdapter.getDefaultAdapter() == null) {
                            Toast.makeText(this, "Sorry, your device does not " +
                                            "support Bluetooth. You will not be able to " +
                                            "open a server and recieve data from scouts.",
                                    Toast.LENGTH_LONG
                            ).show();

                            ((ToggleButton) findViewById(R.id.hostToggle)).
                                    setChecked(false);
                            return;
                        } else {
                            if (BluetoothAdapter.getDefaultAdapter().isEnabled())
                                server.open(selectedEvent);
                            else {
                                Intent enableBtIntent =
                                        new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                startActivityForResult(enableBtIntent, REQUEST_BT_ENABLED);
                            }
                        }

                    } else
                        server.close();
                } else {
                    Toast.makeText(this,
                            "Could not open server. No event selected.",
                            Toast.LENGTH_LONG).show();
                    toggle.setChecked(false);
                }
                break;
            case R.id.excel:
                Intent excelIntent = new Intent(this, ExcelExportDialogActivity.class);
                startActivity(excelIntent);
                break;
            case R.id.dbBackups:
                Intent dbIntent = new Intent(this, DBBackupDialogActivity.class);
                startActivity(dbIntent);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_BT_ENABLED && resultCode == RESULT_OK) {
            Spinner eventChooser = (Spinner) findViewById(R.id.chooseEvent);
            Event selectedEvent = (Event) eventChooser.getSelectedItem();
            if (null != selectedEvent) {
                server.open(selectedEvent);
            }
        }
    }

    /**
     * **
     * Class: GetEventsTask
     * <p/>
     * Description: an AsyncTask to populate the events array and the events spinner.
     */
    private class GetEventsTask extends AsyncTask<Void, Void, Event[]> {

        @Override
        protected Event[] doInBackground(Void... params) {
            return dbManager.getAllEvents();
        }

        @Override
        protected void onPostExecute(Event[] _events) {
            Spinner eventChooser = (Spinner) findViewById(R.id.chooseEvent);
            ArrayAdapter<Event> adapter = new ArrayAdapter<Event>(ServerActivity.this,
                    android.R.layout.simple_spinner_item, _events);
            eventChooser.setAdapter(adapter);
        }
    }
}
