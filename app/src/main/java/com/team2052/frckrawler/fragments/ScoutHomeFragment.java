package com.team2052.frckrawler.fragments;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.crash.FirebaseCrash;
import com.jakewharton.rxbinding.view.RxView;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activities.NavigationDrawerActivity;
import com.team2052.frckrawler.activities.ScoutActivity;
import com.team2052.frckrawler.bluetooth.scout.ScoutSyncHandler;
import com.team2052.frckrawler.bluetooth.scout.events.ScoutSyncCancelledEvent;
import com.team2052.frckrawler.bluetooth.scout.events.ScoutSyncErrorEvent;
import com.team2052.frckrawler.bluetooth.scout.events.ScoutSyncStartEvent;
import com.team2052.frckrawler.bluetooth.scout.events.ScoutSyncSuccessEvent;
import com.team2052.frckrawler.bluetooth.StartBluetoothConnectionEvent;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.util.BluetoothUtil;
import com.team2052.frckrawler.util.ScoutUtil;
import com.team2052.frckrawler.util.SnackbarUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Adam on 11/24/2015.
 */
public class ScoutHomeFragment extends Fragment implements View.OnClickListener {
    private static final int REQUEST_ENABLE_BT = 1;

    private Event mEvent;
    private View syncProgressBar;
    private TextView syncStatusTextView;

    private Subscription syncSubscription;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scout_home, null, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        RxView.clicks(view.findViewById(R.id.scout_match_button)).doOnNext(startScoutingActivity(ScoutActivity.MATCH_SCOUT_TYPE)).subscribe();
        RxView.clicks(view.findViewById(R.id.scout_pit_button)).doOnNext(startScoutingActivity(ScoutActivity.PIT_SCOUT_TYPE)).subscribe();
        //RxView.clicks(view.findViewById(R.id.scout_practice_button)).doOnNext(startScoutingActivity(ScoutActivity.PRACTICE_MATCH_SCOUT_TYPE)).subscribe();
        view.findViewById(R.id.sync_button).setOnClickListener(this);

        syncProgressBar = view.findViewById(R.id.sync_progress_bar);
        syncStatusTextView = (TextView) view.findViewById(R.id.sync_status);

        updateEvent();

        //Disable navigation drawer when scout
        if (ScoutUtil.getDeviceIsScout(getContext())
                && getActivity() instanceof NavigationDrawerActivity) {
            ((NavigationDrawerActivity) getActivity()).setNavigationDrawerEnabled(false);
        }

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        if (syncSubscription != null && !syncSubscription.isUnsubscribed()) {
            syncSubscription.unsubscribe();
        }
        super.onDestroy();
    }

    public Action1<Void> startScoutingActivity(int type) {
        return aVoid -> {
            if (mEvent != null) {
                startActivity(ScoutActivity.newInstance(getActivity(), mEvent, type));
            } else {
                SnackbarUtil.make(getView(), "Unable to find event", Snackbar.LENGTH_LONG).show();
                updateEvent();
            }
        };
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sync_button) {
            if (!BluetoothUtil.hasBluetoothAdapter()) {
                return;
            }

            if (!BluetoothUtil.isBluetoothEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                return;
            }

            ScoutSyncHandler.startScoutSync(getContext(), this::startSync);
        }
    }

    public void enableButtons(boolean enabled) {
        getView().findViewById(R.id.scout_match_button).setEnabled(enabled);
        getView().findViewById(R.id.scout_pit_button).setEnabled(enabled);
    }

    private void updateEvent() {
        setCurrentEvent(ScoutUtil.getScoutEvent(getContext()));
    }

    private void setCurrentEvent(Event scoutEvent) {
        mEvent = scoutEvent;
        enableButtons(mEvent != null);
    }

    private void startSync(BluetoothDevice device) {
        syncSubscription = ScoutSyncHandler.getScoutSyncTask(getContext(), device)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(scoutSyncStatus -> {
                    if (scoutSyncStatus.isSuccessful()) {
                        EventBus.getDefault().post(new ScoutSyncSuccessEvent());
                    } else {
                        EventBus.getDefault().post(new ScoutSyncErrorEvent(scoutSyncStatus.getMessage()));
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                    EventBus.getDefault().post(new ScoutSyncErrorEvent());
                    FirebaseCrash.report(throwable);
                });
    }

    public void setProgressVisibility(int view_state) {
        syncProgressBar.setVisibility(view_state);
        syncStatusTextView.setVisibility(view_state);
    }

    public void setProgressVisibilityDelay(int view_state, int delay) {
        Observable.just(view_state).delay(delay, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::setProgressVisibility);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onEvent(StartBluetoothConnectionEvent event) {
        setProgressVisibility(View.VISIBLE);
        syncStatusTextView.setText("Connecting to device...");
        getView().findViewById(R.id.sync_button).setEnabled(false);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onEvent(ScoutSyncSuccessEvent event) {
        enableButtons(true);
        if (getActivity() instanceof NavigationDrawerActivity) {
            ((NavigationDrawerActivity) getActivity()).setNavigationDrawerEnabled(false);
        }
        setCurrentEvent(ScoutUtil.getScoutEvent(getActivity()));

        getView().findViewById(R.id.sync_button).setEnabled(true);
        syncStatusTextView.setText("Sync Successful");

        //Change Progress bar visibility but delay 1000ms for status to disappear
        syncProgressBar.setVisibility(View.GONE);
        setProgressVisibilityDelay(View.GONE, 1000);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onEvent(ScoutSyncCancelledEvent event) {
        enableButtons(true);
        getView().findViewById(R.id.sync_button).setEnabled(true);
        setProgressVisibility(View.GONE);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onEvent(ScoutSyncErrorEvent event) {
        enableButtons(true);
        getView().findViewById(R.id.sync_button).setEnabled(true);
        setProgressVisibility(View.GONE);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getActivity().getString(R.string.sync_error_title));
        builder.setMessage(event.message == null ? getActivity().getString(R.string.sync_error_message) : event.message);
        builder.setNeutralButton(getString(R.string.close), (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onEvent(ScoutSyncStartEvent event) {
        enableButtons(false);
        setProgressVisibility(View.VISIBLE);
        syncStatusTextView.setText("Starting Sync");
    }
}
