package com.team2052.frckrawler.fragments;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activities.EventInfoActivity;
import com.team2052.frckrawler.activities.ScoutActivity;
import com.team2052.frckrawler.binding.ServerFragmentBinder;
import com.team2052.frckrawler.bluetooth.server.ServerStatus;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.subscribers.EventStringSubscriber;
import com.team2052.frckrawler.util.BluetoothUtil;
import com.team2052.frckrawler.util.SnackbarUtil;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

public class ServerFragment extends BaseDataFragment<List<Event>, List<String>, EventStringSubscriber, ServerFragmentBinder>
        implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private static final String TAG = "ServerFragment";
    private static final int REQUEST_BT_ENABLED = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_server, null, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binder.create();
        binder.setServerFragment(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        binder.updateServerStatus();

        binder.setRootView(view);
        binder.bindViews();

        binder.mHostToggle.setEnabled(BluetoothUtil.hasBluetoothAdapter());
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.host_toggle) {
            if (BluetoothAdapter.getDefaultAdapter() == null) {
                SnackbarUtil.make(getView(), "Sorry, your device does not support bluetooth.", Snackbar.LENGTH_LONG).show();
                return;
            } else if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_BT_ENABLED);
                return;
            }
            buttonView.setChecked(!isChecked);
            if (isEventsValid() && getSelectedEvent() != null) {
                Event event = getSelectedEvent();
                binder.changeServerStatus(event, isChecked);
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
        binder.destroy();
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
        if (binder.eventSpinner.getSelectedItemPosition() < 0) {
            return null;
        }
        return subscriber.getData().get(binder.eventSpinner.getSelectedItemPosition());
    }

    private boolean isEventsValid() {
        return subscriber.getData() != null && !subscriber.getData().isEmpty();
    }

    public static class ServerStatusObserver extends Subscriber<ServerStatus> {

        ServerFragment fragment;

        public ServerStatusObserver(ServerFragment serverFragment) {
            fragment = serverFragment;
        }

        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(ServerStatus serverStatus) {
            fragment.binder.mHostToggle.setOnCheckedChangeListener(null);
            fragment.binder.mHostToggle.setChecked(serverStatus.getStatus());
            fragment.binder.mHostToggle.setOnCheckedChangeListener(fragment);

            int index = 0;
            if (serverStatus.getEvent() != null) {
                for (int i = 0; i < fragment.subscriber.getData().size(); i++) {
                    if (fragment.subscriber.getData().get(i).getId() == serverStatus.getEvent().getId()) {
                        index = i;
                        break;
                    }
                }
            }

            fragment.binder.setSelection(index);
        }
    }


}
