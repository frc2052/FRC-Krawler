package com.team2052.frckrawler.fragments;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding.view.RxView;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activities.HasComponent;
import com.team2052.frckrawler.activities.NavigationDrawerActivity;
import com.team2052.frckrawler.activities.ScoutActivity;
import com.team2052.frckrawler.bluetooth.client.ScoutSyncHandler;
import com.team2052.frckrawler.bluetooth.client.events.ScoutSyncCancelledEvent;
import com.team2052.frckrawler.bluetooth.client.events.ScoutSyncErrorEvent;
import com.team2052.frckrawler.bluetooth.client.events.ScoutSyncStartEvent;
import com.team2052.frckrawler.bluetooth.client.events.ScoutSyncSuccessEvent;
import com.team2052.frckrawler.database.RxDBManager;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.di.FragmentComponent;
import com.team2052.frckrawler.util.BluetoothUtil;
import com.team2052.frckrawler.util.ScoutUtil;
import com.team2052.frckrawler.util.SnackbarUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

import rx.functions.Action1;

/**
 * Created by Adam on 11/24/2015.
 */
public class ScoutHomeFragment extends Fragment implements View.OnClickListener {
    private static final int REQUEST_ENABLE_BT = 1;

    @Inject
    RxDBManager rxDbManager;
    @Inject
    ScoutSyncHandler scoutSyncHandler;

    private FragmentComponent mComponent;
    private Event mEvent;
    private View syncButton, syncProgressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() instanceof HasComponent) {
            mComponent = ((HasComponent) getActivity()).getComponent();
        }
        mComponent.inject(this);

        setHasOptionsMenu(true);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scout_home, null, false);
    }

    public Action1<Void> startScoutingActivity(int type) {
        return aVoid -> {
            if (mEvent != null) {
                startActivity(ScoutActivity.newInstance(getActivity(), mEvent, type));
            } else {
                SnackbarUtil.make(getView(), "Unable to find event", Snackbar.LENGTH_LONG).show();
                setCurrentEvent(ScoutUtil.getScoutEvent(getContext()));
            }
        };
    }

    @Override

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        RxView.clicks(view.findViewById(R.id.scout_match_button)).doOnNext(startScoutingActivity(ScoutActivity.MATCH_SCOUT_TYPE)).subscribe();
        RxView.clicks(view.findViewById(R.id.scout_pit_button)).doOnNext(startScoutingActivity(ScoutActivity.PIT_SCOUT_TYPE)).subscribe();
        //RxView.clicks(view.findViewById(R.id.scout_practice_button)).doOnNext(startScoutingActivity(ScoutActivity.PRACTICE_MATCH_SCOUT_TYPE)).subscribe();
        view.findViewById(R.id.sync_button).setOnClickListener(this);

        setCurrentEvent(ScoutUtil.getScoutEvent(getContext()));

        syncButton = view.findViewById(R.id.sync_button);

        RxView.clicks(syncButton).map(aVoid -> {
            if (!BluetoothUtil.hasBluetoothAdapter()) {
                throw new RuntimeException("Bluetooth is not supported");
            }

            return aVoid;
        });


        syncProgressBar = view.findViewById(R.id.sync_progress_bar);

        if (ScoutUtil.getDeviceIsScout(getContext()) && getActivity() instanceof NavigationDrawerActivity) {
            ((NavigationDrawerActivity) getActivity()).setNavigationDrawerEnabled(false);
        }

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sync_button) {
            if (BluetoothUtil.hasBluetoothAdapter()) {
                if (!BluetoothUtil.isBluetoothEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                } else {
                    scoutSyncHandler.startScoutSync(getContext());
                }
            }
        }
    }

    public void enableButtons(boolean enabled) {
        if (getView() == null) {
            return;
        }
        getView().findViewById(R.id.scout_match_button).setEnabled(enabled);
        getView().findViewById(R.id.scout_pit_button).setEnabled(enabled);
        //getView().findViewById(R.id.scout_practice_button).setEnabled(enabled);
    }

    private void setCurrentEvent(Event scoutEvent) {
        mEvent = scoutEvent;
        enableButtons(mEvent != null);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onEvent(ScoutSyncSuccessEvent event) {
        enableButtons(true);
        if (getActivity() instanceof NavigationDrawerActivity) {
            ((NavigationDrawerActivity) getActivity()).setNavigationDrawerEnabled(false);
        }
        setProgressVisibility(View.GONE);
        SnackbarUtil.make(getView(), "Sync Successful", Snackbar.LENGTH_LONG).show();
        setCurrentEvent(ScoutUtil.getScoutEvent(getActivity()));
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onEvent(ScoutSyncCancelledEvent event) {
        enableButtons(true);
        setProgressVisibility(View.GONE);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onEvent(ScoutSyncErrorEvent event) {
        enableButtons(true);
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
        SnackbarUtil.make(getView(), "Starting Sync", Snackbar.LENGTH_SHORT).show();
        setProgressVisibility(View.VISIBLE);
    }

    public void setProgressVisibility(int view_state) {
        syncButton.setVisibility(view_state == View.VISIBLE ? View.GONE : View.VISIBLE);
        syncProgressBar.setVisibility(view_state);
    }
}
