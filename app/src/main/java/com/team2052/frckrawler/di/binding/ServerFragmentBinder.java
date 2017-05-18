package com.team2052.frckrawler.di.binding;

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
import android.widget.TextView;

import com.google.firebase.crash.FirebaseCrash;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.bluetooth.server.ServerStatus;
import com.team2052.frckrawler.fragments.ServerFragment;
import com.team2052.frckrawler.models.Event;
import com.team2052.frckrawler.services.ServerService;
import com.team2052.frckrawler.views.MessageCardView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;

public class ServerFragmentBinder extends BaseDataBinder<List<String>> {
    @BindView(R.id.event_spinner)
    public Spinner eventSpinner;
    @BindView(R.id.host_toggle)
    public SwitchCompat mHostToggle;
    @BindView(R.id.server_status)
    public TextView mServerStatus;
    @BindView(R.id.view_event)
    Button viewEventButton;
    @BindView(R.id.scout_match_button)
    Button scoutMatchButton;
    @BindView(R.id.scout_pit_button)
    Button scoutPitButton;
    //@BindView(R.id.scout_practice_button)
    //Button scoutPracticeButton;
    //@BindView(R.id.server_event_container)
    //View mServerEventContainer;
    @BindView(R.id.view_logs)
    Button viewLogsButton;
    @BindView(R.id.view_game)
    Button viewGameButton;
    @BindView(R.id.message_card)
    MessageCardView messageCardView;

    private ServerService serverService;
    private boolean mBound = false;
    private ServerFragment serverFragment;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            serverService = ((ServerService.ServerServiceBinder) service).getService();
            serverService.toObservable()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ServerStatusObserver());
            mBound = true;
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
            messageCardView.setMessageType(MessageCardView.ERROR);
            messageCardView.setMessageTitle("No Events");
            messageCardView.setMessageText("No events downloaded. Please download an event to get started");
            showData(false);
            return;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(mActivity, android.R.layout.simple_list_item_1, data);
        eventSpinner.setAdapter(adapter);
        eventSpinner.setSelection(selection);

        showData(true);
    }

    private void showData(boolean show) {
        viewLogsButton.setEnabled(show);
        viewEventButton.setEnabled(show);
        viewGameButton.setEnabled(show);
        scoutMatchButton.setEnabled(show);
        scoutPitButton.setEnabled(show);

        eventSpinner.setEnabled(show);
        mHostToggle.setEnabled(show);

        if (show) {
            messageCardView.setVisibility(View.GONE);
            eventSpinner.setVisibility(View.VISIBLE);
        } else {
            messageCardView.setVisibility(View.VISIBLE);
            eventSpinner.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void bindViews() {
        ButterKnife.bind(this, mRootView);

        viewEventButton.setOnClickListener(serverFragment);
        scoutMatchButton.setOnClickListener(serverFragment);
        scoutPitButton.setOnClickListener(serverFragment);
        viewLogsButton.setOnClickListener(serverFragment);
        viewGameButton.setOnClickListener(serverFragment);
        //scoutPracticeButton.setOnClickListener(serverFragment);
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
            serverService.changeServerStatus(event, isChecked);
        }
    }

    public class ServerStatusObserver implements Observer<ServerStatus> {
        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable e) {
            e.printStackTrace();
            FirebaseCrash.report(e);
        }

        @Override
        public void onNext(ServerStatus serverStatus) {
            mHostToggle.setOnCheckedChangeListener(null);
            mHostToggle.setChecked(serverStatus.state());
            eventSpinner.setEnabled(!serverStatus.state());

            mHostToggle.setOnCheckedChangeListener(ServerFragmentBinder.this.serverFragment);
            mServerStatus.setText(serverStatus.state() ? R.string.server_status_on : R.string.server_status_off);

            int index = serverStatus.findEventIndex(ServerFragmentBinder.this.serverFragment.getData());
            setSelection(index);
        }
    }
}
