package com.team2052.frckrawler.fragments;

import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Spinner;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activities.EventInfoActivity;
import com.team2052.frckrawler.activities.ScoutActivity;
import com.team2052.frckrawler.bluetooth.server.ServerService;
import com.team2052.frckrawler.bluetooth.server.ServerStatus;
import com.team2052.frckrawler.consumer.ServerFragmentConsumer;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.subscribers.EventStringSubscriber;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

public class ServerFragment extends BaseDataFragment<List<Event>, List<String>, EventStringSubscriber, ServerFragmentConsumer>
        implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private static final String TAG = "ServerFragment";
    private static final int REQUEST_BT_ENABLED = 1;

    private SwitchCompat mHostToggle;
    private Spinner mEventSpinner;
    private ServerService boundService;
    private boolean mIsBound = false;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            boundService = ((ServerService.ServerServiceBinder) service).getService();
            mIsBound = true;
            boundService.getServerStatus().subscribeOn(AndroidSchedulers.mainThread()).subscribe(serverStatusSubscriber);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            boundService = null;
        }
    };

    private Subscriber<ServerStatus> serverStatusSubscriber = new Subscriber<ServerStatus>() {
        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable e) {
        }

        @Override
        public void onNext(ServerStatus serverStatus) {
            mHostToggle.setOnCheckedChangeListener(null);
            mHostToggle.setChecked(serverStatus.getStatus());
            mHostToggle.setOnCheckedChangeListener(ServerFragment.this);

            int index = 0;
            if (serverStatus.getEvent() != null) {
                for (int i = 0; i < subscriber.getData().size(); i++) {
                    if (subscriber.getData().get(i).getId() == serverStatus.getEvent().getId()) {
                        index = i;
                        break;
                    }
                }
            }

            binder.eventSpinner.setSelection(index);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Bind to the service
        getActivity().getApplicationContext().bindService(new Intent(getActivity(), ServerService.class), mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_server, null, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        binder.setRootView(view);
        subscriber.bindViewsIfNeeded();

        mHostToggle = (SwitchCompat) view.findViewById(R.id.host_toggle);
        mHostToggle.setOnCheckedChangeListener(this);
        mEventSpinner = (Spinner) view.findViewById(R.id.event_spinner);

        view.findViewById(R.id.view_event).setOnClickListener(this);
        view.findViewById(R.id.scout_match_button).setOnClickListener(this);
        view.findViewById(R.id.scout_pit_button).setOnClickListener(this);
        view.findViewById(R.id.scout_practice_button).setOnClickListener(this);

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mIsBound){
            boundService.getServerStatus().subscribeOn(AndroidSchedulers.mainThread()).subscribe(serverStatusSubscriber);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.host_toggle) {
            if (BluetoothAdapter.getDefaultAdapter() == null) {
                Snackbar.make(getView(), "Sorry, your device does not support bluetooth.", Snackbar.LENGTH_LONG).show();
                return;
            } else if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_BT_ENABLED);
                return;
            }
            buttonView.setChecked(!isChecked);
            if (isEventsValid() && getSelectedEvent() != null) {
                Event event = getSelectedEvent();
                boundService.changeServerStatus(event, isChecked).subscribe(serverStatusSubscriber);
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (isEventsValid() && getSelectedEvent() != null) {
            switch (view.getId()) {
                case R.id.view_event:
                    startActivity(EventInfoActivity.newInstance(getActivity(), getSelectedEvent().getId()));
                    return;
                case R.id.scout_match_button:
                    startActivity(ScoutActivity.newInstance(getActivity(), getSelectedEvent(), ScoutActivity.MATCH_SCOUT_TYPE));
                    return;
                case R.id.scout_pit_button:
                    startActivity(ScoutActivity.newInstance(getActivity(), getSelectedEvent(), ScoutActivity.PIT_SCOUT_TYPE));
                    return;
                case R.id.scout_practice_button:
                    startActivity(ScoutActivity.newInstance(getActivity(), getSelectedEvent(), ScoutActivity.PRACTICE_MATCH_SCOUT_TYPE));
            }
        }
    }

    @Override
    public void onDestroy() {
        if (mIsBound) {
            getActivity().getApplicationContext().unbindService(mConnection);
            mIsBound = false;
        }
        super.onDestroy();
    }

    @Override
    public void inject() {
        mComponent.inject(this);
    }

    @Override
    protected Observable<? extends List<Event>> getObservable() {
        return dbManager.allEvents();
    }

    private Event getSelectedEvent() {
        if (mEventSpinner.getSelectedItemPosition() < 0) {
            return null;
        }
        return subscriber.getData().get(mEventSpinner.getSelectedItemPosition());
    }

    private boolean isEventsValid() {
        return subscriber.getData() != null && !subscriber.getData().isEmpty();
    }
}
