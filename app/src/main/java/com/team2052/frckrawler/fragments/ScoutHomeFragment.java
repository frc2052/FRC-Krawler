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

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activities.HasComponent;
import com.team2052.frckrawler.activities.ScoutActivity;
import com.team2052.frckrawler.bluetooth.client.ScoutSyncHandler;
import com.team2052.frckrawler.bluetooth.client.events.ScoutSyncCancelledEvent;
import com.team2052.frckrawler.bluetooth.client.events.ScoutSyncErrorEvent;
import com.team2052.frckrawler.bluetooth.client.events.ScoutSyncStartEvent;
import com.team2052.frckrawler.bluetooth.client.events.ScoutSyncSuccessEvent;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.di.FragmentComponent;
import com.team2052.frckrawler.util.BluetoothUtil;
import com.team2052.frckrawler.util.ScoutUtil;
import com.team2052.frckrawler.util.SnackbarUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by Adam on 11/24/2015.
 */
public class ScoutHomeFragment extends Fragment implements View.OnClickListener {
    private static final int REQUEST_ENABLE_BT = 1;
    DBManager dbManager;
    private ScoutSyncHandler scoutSyncHandler;
    private FragmentComponent mComponent;
    private Event mEvent;
    private View syncButton, syncProgressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() instanceof HasComponent) {
            mComponent = ((HasComponent) getActivity()).getComponent();
        }
        dbManager = mComponent.dbManager();
        scoutSyncHandler = mComponent.scoutSyncHander();
        setCurrentEvent(ScoutUtil.getScoutEvent(getContext()));
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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        view.findViewById(R.id.scout_match_button).setOnClickListener(this);
        view.findViewById(R.id.scout_pit_button).setOnClickListener(this);
        view.findViewById(R.id.scout_practice_button).setOnClickListener(this);
        view.findViewById(R.id.sync_button).setOnClickListener(this);

        syncButton = view.findViewById(R.id.sync_button);
        syncProgressBar = view.findViewById(R.id.sync_progress_bar);

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
        } else {
            if (mEvent != null) {
                switch (v.getId()) {
                    case R.id.scout_match_button:
                        startActivity(ScoutActivity.newInstance(getActivity(), mEvent, ScoutActivity.MATCH_SCOUT_TYPE));
                        break;
                    case R.id.scout_pit_button:
                        startActivity(ScoutActivity.newInstance(getActivity(), mEvent, ScoutActivity.PIT_SCOUT_TYPE));
                        break;
                    case R.id.scout_practice_button:
                        startActivity(ScoutActivity.newInstance(getActivity(), mEvent, ScoutActivity.PRACTICE_MATCH_SCOUT_TYPE));
                        break;
                }
            } else {
                SnackbarUtil.make(getView(), "Unable to find event", Snackbar.LENGTH_LONG).show();
                //Try to reload the event
                setCurrentEvent(ScoutUtil.getScoutEvent(getContext()));
            }
        }
    }

    private void setCurrentEvent(Event scoutEvent) {
        mEvent = scoutEvent;
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onEvent(ScoutSyncSuccessEvent event) {
        setProgressVisibility(View.GONE);
        SnackbarUtil.make(getView(), "Sync Successful", Snackbar.LENGTH_LONG).show();
        setCurrentEvent(ScoutUtil.getScoutEvent(getActivity()));
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onEvent(ScoutSyncCancelledEvent event) {
        setProgressVisibility(View.GONE);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onEvent(ScoutSyncErrorEvent event) {
        setProgressVisibility(View.GONE);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getActivity().getString(R.string.sync_error_title));
        builder.setMessage(event.message == null ? getActivity().getString(R.string.sync_error_message) : event.message);
        builder.setNeutralButton(getString(R.string.close), (dialog, which) -> {
            dialog.dismiss();
        });
        builder.show();
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onEvent(ScoutSyncStartEvent event) {
        SnackbarUtil.make(getView(), "Starting Sync", Snackbar.LENGTH_SHORT).show();
        setProgressVisibility(View.VISIBLE);
    }

    public void setProgressVisibility(int view_state) {
        syncButton.setVisibility(view_state == View.VISIBLE ? View.GONE : View.VISIBLE);
        syncProgressBar.setVisibility(view_state);
    }
}
