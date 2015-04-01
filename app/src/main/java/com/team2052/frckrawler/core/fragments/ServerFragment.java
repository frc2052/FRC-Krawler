package com.team2052.frckrawler.core.fragments;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
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
import com.team2052.frckrawler.core.fragments.dialog.process.ExportDialogFragment;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.server.ServerService;
import com.team2052.frckrawler.server.events.ServerStateChangeEvent;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class ServerFragment extends BaseFragment implements View.OnClickListener {
    private static final int REQUEST_BT_ENABLED = 1;
    @InjectView(R.id.chooseEvent)
    Spinner eventSpinner;
    @InjectView(R.id.hostToggle)
    SwitchCompat mHostToggle;
    @InjectView(R.id.server_setting_compile_weight)
    EditText compileWeight;
    private List<Event> mEvents;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBus.getDefault().register(this);

    }

    public void onEvent(ServerStateChangeEvent serverStateChangeEvent){
        if(getView() != null){
            ((SwitchCompat) getView().findViewById(R.id.hostToggle)).setChecked(serverStateChangeEvent.getState());
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        this.getActivity().bindService(new Intent(getActivity(), ServerService.class), mConnection, Context.BIND_AUTO_CREATE);

    }

    private Messenger mService;
    private boolean mBound;
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = new Messenger(service);
            mBound = true;
            serverState();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            mBound = false;
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_server, null);
        ButterKnife.inject(this, v);
        v.findViewById(R.id.excel).setOnClickListener(this);
        //v.findViewById(R.id.dbBackups).setOnClickListener(this);
        v.findViewById(R.id.hostToggle).setOnClickListener(this);
        v.findViewById(R.id.hostToggle).setOnClickListener(this);
        //v.findViewById(R.id.pick_list).setOnClickListener(this);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(GlobalValues.PREFS_FILE_NAME, 0);
        compileWeight.setText(Float.toString(sharedPreferences.getFloat(GlobalValues.PREFS_COMPILE_WEIGHT, 1.0f)));
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new GetEventsTask().execute();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.hostToggle:
                openServer();
                break;
            case R.id.excel:
                if (getSelectedEvent() != null) {
                    ExportDialogFragment.newInstance(getSelectedEvent()).show(getChildFragmentManager(), "exportDialogProgress");
                }
                break;
            /*case R.id.pick_list:
                if (getSelectedEvent() != null) {
                    Intent intent = PicklistActivity.newInstance(getActivity(), getSelectedEvent());
                    startActivity(intent);
                }
                break;*/
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

                if (BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                    openServer(selectedEvent);
                } else {
                    this.startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_BT_ENABLED);
                }

            } else {
                closeServer();
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
                openServer(selectedEvent);
            }
        }
    }

    public void openServer(Event event) {
        Message msg = Message.obtain(null, ServerService.MSG_START_SEREVR);

        Bundle bundle = new Bundle();
        bundle.putLong(ServerService.EVENT_ID_EXTRA, event.getId());
        msg.setData(bundle);

        try {
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void serverState() {
        Message msg = Message.obtain(null, ServerService.MSG_STATE);
        try {
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void closeServer() {
        Message msg = Message.obtain(null, ServerService.MSG_STOP_SEREVR);
        try {
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    private Event getSelectedEvent() {
        if (mEvents == null) {
            return null;
        } else if (mEvents.size() == 0) {
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


    private class GetEventsTask extends AsyncTask<Void, Void, List<Event>> {

        @Override
        protected List<Event> doInBackground(Void... params) {
            return mDbManager.getDaoSession().getEventDao().loadAll();
        }

        @Override
        protected void onPostExecute(List<Event> _events) {
            if (getView() != null) {
                Spinner eventChooser = (Spinner) getView().findViewById(R.id.chooseEvent);
                mEvents = _events;
                List<String> eventNames = new ArrayList<>();
                for (Event event : _events) {
                    eventNames.add(mDbManager.getDaoSession().getGameDao().load(event.getGameId()).getName() + ", " + event.getName());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, eventNames);
                eventChooser.setAdapter(adapter);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mBound) {
            getActivity().unbindService(mConnection);
            mBound = false;
        }
    }
}
