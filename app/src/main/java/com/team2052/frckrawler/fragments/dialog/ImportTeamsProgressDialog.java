package com.team2052.frckrawler.fragments.dialog;

import android.os.Bundle;

import com.team2052.frckrawler.models.Event;

import rx.Subscription;

public class ImportTeamsProgressDialog extends BaseProgressDialog {
    private static final String TAG = "ImportTeamsProgressDial";

    public static String TEAMS_ARGUMENT = "TEAMS";
    public static String EVENT_ID_ARGUMENT = "EVENT_ID";
    private Subscription subscription;


    public static ImportTeamsProgressDialog newInstance(String teams, long event_id) {
        ImportTeamsProgressDialog importTeamsProgressDialog = new ImportTeamsProgressDialog();
        Bundle bundle = new Bundle();
        bundle.putString(TEAMS_ARGUMENT, teams);
        bundle.putLong(EVENT_ID_ARGUMENT, event_id);
        importTeamsProgressDialog.setArguments(bundle);
        return importTeamsProgressDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String teams = getArguments().getString(TEAMS_ARGUMENT);
        Event event = mRxDbManager.getEventsTable().load(getArguments().getLong(EVENT_ID_ARGUMENT));
        //TODO
//        subscription = Observable.just(teams)
//                .map(teamsString -> Arrays.asList(teamsString.split("\\s*,\\s*")))
//                .flatMap(Observable::from)
//                .map(Integer::parseInt)
//                .map(teamNumber -> String.format(TBA.TEAM, teamNumber))
//                .map(HTTP::getResponse)
//                .map(HTTP::dataFromResponse)
//                .map(JSON::getAsJsonObject)
//                .map(jsonObject -> JSON.getGson().fromJson(jsonObject, Team.class))
//                .toList()
//                .flatMap(Observable::from)
//                .map(team -> {
//                    AndroidSchedulers.mainThread().createWorker().schedule(() -> {
//                                if (getDialog() != null) {
//                                    ((ProgressDialog) getDialog()).setMessage("Inserting Team " + team.getNumber());
//                                }
//                            }
//                    );
//                    mRxDbManager.getTeamsTable().insertNew(team, event);
//                    return true;
//                })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .toList()
//                .subscribe(onNext -> {
//                    dismiss();
//                    if (getParentFragment() instanceof RefreshListener) {
//                        ((RefreshListener) getParentFragment()).refresh();
//                    }
//                }, onError -> {
//                    Log.e(TAG, "onCreate: ", onError);
//                    dismiss();
//                });

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
