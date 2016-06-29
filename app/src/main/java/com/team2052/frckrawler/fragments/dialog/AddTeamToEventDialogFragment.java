package com.team2052.frckrawler.fragments.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.squareup.okhttp.Response;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activities.DatabaseActivity;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.Team;
import com.team2052.frckrawler.listeners.RefreshListener;
import com.team2052.frckrawler.tba.HTTP;
import com.team2052.frckrawler.tba.JSON;
import com.team2052.frckrawler.tba.TBA;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author Adam
 * @since 3/7/2015.
 */
public class AddTeamToEventDialogFragment extends android.support.v4.app.DialogFragment implements DialogInterface.OnClickListener {
    @BindView(R.id.team_number)
    EditText add_team;
    private Event mEvent;
    private DBManager dbManager;

    public static AddTeamToEventDialogFragment newInstance(Event game) {
        AddTeamToEventDialogFragment fragment = new AddTeamToEventDialogFragment();
        Bundle b = new Bundle();
        b.putLong(DatabaseActivity.PARENT_ID, game.getId());
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbManager = DBManager.getInstance(getActivity());
        this.mEvent = DBManager.getInstance(getActivity()).getEventsTable().load(getArguments().getLong(DatabaseActivity.PARENT_ID));
    }

    @Override
    //Build the dialog
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_add_team, null);
        ButterKnife.bind(this, view);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppAlertDialogStyle);
        builder.setTitle("Add Team");
        builder.setPositiveButton("Add", this);
        builder.setNegativeButton("Cancel", this);
        builder.setView(view);
        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            Observable.defer(() -> Observable.just(Integer.parseInt(add_team.getText().toString())))
                    .map(teamNumber -> String.format(TBA.TEAM, teamNumber))
                    .map(HTTP::getResponse)
                    .filter(Response::isSuccessful)
                    .map(HTTP::dataFromResponse)
                    .map(responseString -> JSON.getAsJsonObject(responseString))
                    .map(jsonObject -> JSON.getGson().fromJson(jsonObject, Team.class))
                    .map(team -> {
                        dbManager.getTeamsTable().insertNew(team, mEvent);
                        return true;
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(onNext -> {
                        if(getParentFragment() instanceof RefreshListener) {
                            ((RefreshListener) getParentFragment()).refresh();
                        }
                    }, onError -> {
                    }, () -> {
                    });
        } else {
            dismiss();
        }
    }
}
