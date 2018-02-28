package com.team2052.frckrawler.fragments.scout;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activities.NavigationDrawerActivity;
import com.team2052.frckrawler.activities.ScoutActivity;
import com.team2052.frckrawler.bluetooth.StartBluetoothConnectionEvent;
import com.team2052.frckrawler.bluetooth.scout.ScoutSyncHandler;
import com.team2052.frckrawler.bluetooth.scout.events.ScoutSyncCancelledEvent;
import com.team2052.frckrawler.bluetooth.scout.events.ScoutSyncErrorEvent;
import com.team2052.frckrawler.bluetooth.scout.events.ScoutSyncStartEvent;
import com.team2052.frckrawler.bluetooth.scout.events.ScoutSyncSuccessEvent;
import com.team2052.frckrawler.helpers.BluetoothHelper;
import com.team2052.frckrawler.helpers.ScoutHelper;
import com.team2052.frckrawler.helpers.SnackbarHelper;
import com.team2052.frckrawler.models.Event;
import com.team2052.frckrawler.services.StartSyncIntentService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class ScoutHomeFragment extends Fragment implements View.OnClickListener {
    private static final int REQUEST_ENABLE_BT = 1;

    private Event mEvent;
    private View syncProgressBar;
    private TextView syncStatusTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_scout_home, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.change_scout_theme) {
            ScoutHelper.showAskThemeDialog(getContext());
        }
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scout_home, null, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        RxView.clicks(view.findViewById(R.id.scout_match_button)).doOnNext(startScoutingActivity(ScoutActivity.Companion.getMATCH_SCOUT_TYPE())).subscribe();
        RxView.clicks(view.findViewById(R.id.scout_pit_button)).doOnNext(startScoutingActivity(ScoutActivity.Companion.getPIT_SCOUT_TYPE())).subscribe();
        //RxView.clicks(view.findViewById(R.id.scout_practice_button)).doOnNext(startScoutingActivity(ScoutActivity.PRACTICE_MATCH_SCOUT_TYPE)).subscribe();
        view.findViewById(R.id.sync_button).setOnClickListener(this);

        syncProgressBar = view.findViewById(R.id.sync_progress_bar);
        syncStatusTextView = (TextView) view.findViewById(R.id.sync_status);

        updateEvent();

        //Disable navigation drawer when scout
        if (ScoutHelper.getDeviceIsScout(getContext()) && getActivity() instanceof NavigationDrawerActivity) {
            ((NavigationDrawerActivity) getActivity()).setNavigationDrawerEnabled(false);
        }

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    public Action1<Void> startScoutingActivity(int type) {
        return aVoid -> {
            if (mEvent != null) {
                startActivity(ScoutActivity.Companion.newInstance(getActivity(), mEvent, type));
            } else {
                SnackbarHelper.make(getView(), "Unable to find event", Snackbar.LENGTH_LONG).show();
                updateEvent();
            }
        };
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sync_button) {
            if (!BluetoothHelper.hasBluetoothAdapter()) {
                return;
            }

            if (!BluetoothHelper.isBluetoothEnabled()) {
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
        setCurrentEvent(ScoutHelper.getScoutEvent(getContext()));
    }

    private void setCurrentEvent(Event scoutEvent) {
        mEvent = scoutEvent;
        enableButtons(mEvent != null);
    }

    private void startSync(String deviceMacAddress) {
        Intent intent = new Intent(getContext(), StartSyncIntentService.class);
        intent.putExtra(StartSyncIntentService.Companion.getMAC_ADDRESS_KEY(), deviceMacAddress);
        getActivity().startService(intent);
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

        updateEvent();
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
