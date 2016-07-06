package com.team2052.frckrawler.binding;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.bluetooth.server.ServerService;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.fragments.ServerFragment;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;

public class ServerFragmentBinder extends BaseDataBinder<List<String>> {
    @BindView(R.id.event_spinner)
    public Spinner eventSpinner;
    @BindView(R.id.host_toggle)
    public SwitchCompat mHostToggle;
    @BindView(R.id.view_event)
    Button viewEventButton;
    @BindView(R.id.scout_match_button)
    Button scoutMatchButton;
    @BindView(R.id.scout_pit_button)
    Button scoutPitButton;
    @BindView(R.id.scout_practice_button)
    Button scoutPracticeButton;
    @BindView(R.id.server_event_container)
    View mServerEventContainer;
    @BindView(R.id.server_events_error)
    View mServerEventsError;
    @BindView(R.id.scout_server_card)
    View mScoutServerCard;

    private ServerService serverService;
    private boolean mBound = false;
    private ServerFragment serverFragment;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            serverService = ((ServerService.ServerServiceBinder) service).getService();
            mBound = true;
            updateServerStatus();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serverService = null;
        }
    };
    private int selection;

    @Override
    public void updateData(List<String> data) {
        if (data == null || eventSpinner == null)
            return;
        if (data.isEmpty()) {
            showData(false);
            return;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(mActivity, android.R.layout.simple_list_item_1, data);
        eventSpinner.setAdapter(adapter);
        eventSpinner.setSelection(selection);

        showData(true);

        updateServerStatus();

    }

    public void updateServerStatus() {
        if (mBound && serverService != null) {
            serverService.getServerStatus().observeOn(AndroidSchedulers.mainThread()).subscribe(new ServerFragment.ServerStatusObserver(serverFragment));
        }
    }


    private void showData(boolean show) {
        if (show) {
            mServerEventContainer.setVisibility(View.VISIBLE);
            mScoutServerCard.setVisibility(View.VISIBLE);
            mServerEventsError.setVisibility(View.GONE);
        } else {
            mServerEventContainer.setVisibility(View.GONE);
            mScoutServerCard.setVisibility(View.GONE);
            mServerEventsError.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void bindViews() {
        ButterKnife.bind(this, rootView);

        viewEventButton.setOnClickListener(serverFragment);
        scoutMatchButton.setOnClickListener(serverFragment);
        scoutPitButton.setOnClickListener(serverFragment);
        scoutPracticeButton.setOnClickListener(serverFragment);
        mHostToggle.setOnCheckedChangeListener(serverFragment);
    }

    public void create() {
        mActivity.getApplicationContext().bindService(new Intent(mActivity, ServerService.class), mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void destroy() {
        if (mBound) {
            mActivity.getApplicationContext().unbindService(mServiceConnection);
            mBound = false;
        }
    }

    public void setServerFragment(ServerFragment serverFragment) {
        this.serverFragment = serverFragment;
    }

    public void setSelection(int selection) {
        this.selection = selection;
        eventSpinner.setSelection(selection);
    }

    public void changeServerStatus(Event event, boolean isChecked) {
        if (serverService != null) {
            serverService.changeServerStatus(event, isChecked).observeOn(AndroidSchedulers.mainThread()).subscribe(new ServerFragment.ServerStatusObserver(serverFragment));
        }
    }
}
