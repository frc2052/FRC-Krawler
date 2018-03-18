package com.team2052.frckrawler.fragments.dialog;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;

import com.team2052.frckrawler.data.tba.v3.TBA;
import com.team2052.frckrawler.interfaces.RefreshListener;

import java.util.Arrays;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ImportTeamsProgressDialog extends BaseProgressDialog {
    private static final String TAG = "ImportTeamsProgressDial";

    public static String TEAMS_ARGUMENT = "TEAMS";
    public static String EVENT_ID_ARGUMENT = "EVENT_ID";
    private Subscription subscription;


    public static ImportTeamsProgressDialog newInstance(String teams) {
        ImportTeamsProgressDialog importTeamsProgressDialog = new ImportTeamsProgressDialog();
        Bundle bundle = new Bundle();
        bundle.putString(TEAMS_ARGUMENT, teams);
        importTeamsProgressDialog.setArguments(bundle);
        return importTeamsProgressDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String teams = getArguments().getString(TEAMS_ARGUMENT);
        subscription = Observable.just(teams)
                .map(teamsString -> Arrays.asList(teamsString.split("\\s*,\\s*")))
                .flatMap(Observable::from)
                .concatMap(TBA::requestTeam)
                .map(team -> {
                    AndroidSchedulers.mainThread().createWorker().schedule(() -> {
                                if (getDialog() != null) {
                                    ((ProgressDialog) getDialog()).setMessage("Inserting Team " + team.getNumber());
                                }
                            }
                    );
                    mRxDbManager.getTeamsTable().insertNew(team);
                    return true;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .toList()
                .subscribe(onNext -> {
                    dismiss();
                    if (getParentFragment() instanceof RefreshListener) {
                        ((RefreshListener) getParentFragment()).refresh();
                    }
                }, onError -> {
                    Log.e(TAG, "onCreate: ", onError);
                    dismiss();
                });

    }

    @Override
    public CharSequence getMessage() {
        return "Downloading Data...";
    }

    @Override
    public void onDestroy() {
        if (subscription != null) {
            subscription.unsubscribe();
        }
        super.onDestroy();
    }
}
