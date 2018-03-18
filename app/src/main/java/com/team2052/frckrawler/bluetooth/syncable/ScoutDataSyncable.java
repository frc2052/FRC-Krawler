package com.team2052.frckrawler.bluetooth.syncable;

import android.content.Context;

import com.team2052.frckrawler.bluetooth.BluetoothConstants;
import com.team2052.frckrawler.data.RxDBManager;
import com.team2052.frckrawler.models.Metric;
import com.team2052.frckrawler.models.Team;

import java.util.List;


public class ScoutDataSyncable extends ScoutSyncable {
    private final List<Metric> metrics;
    private final List<Team> teams;

    public ScoutDataSyncable(Context context) {
        super(BluetoothConstants.ReturnCodes.OK);
        RxDBManager dbManager = RxDBManager.Companion.getInstance(context);
        metrics = dbManager.getMetricsTable().loadAll();
        teams = dbManager.getTeamsTable().loadAll();
    }

    @Override
    public void saveToScout(Context context) {
        RxDBManager dbManager = RxDBManager.Companion.getInstance(context);

        dbManager.runInTx(() -> {
                    for (int i = 0; i < metrics.size(); i++) {
                        dbManager.getMetricsTable().insert(metrics.get(i));
                    }

                    for (int i = 0; i < teams.size(); i++) {
                        dbManager.getTeamsTable().insert(teams.get(i));
                    }
                }
        );
    }
}
