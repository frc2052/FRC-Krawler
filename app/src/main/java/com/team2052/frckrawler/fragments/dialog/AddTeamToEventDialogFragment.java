package com.team2052.frckrawler.fragments.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activities.DatabaseActivity;
import com.team2052.frckrawler.data.RxDBManager;
import com.team2052.frckrawler.models.Event;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

/**
 * @author Adam
 * @since 3/7/2015.
 */
public class AddTeamToEventDialogFragment extends android.support.v4.app.DialogFragment implements DialogInterface.OnClickListener {
    private static final String TAG = "AddTeamToEventDialogFra";

    @BindView(R.id.team_number)
    TextInputLayout add_team;
    private Event mEvent;
    private RxDBManager rxDbManager;

    public static AddTeamToEventDialogFragment newInstance(Event game) {
        AddTeamToEventDialogFragment fragment = new AddTeamToEventDialogFragment();
        Bundle b = new Bundle();
        b.putLong(DatabaseActivity.Companion.getPARENT_ID(), game.getId());
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rxDbManager = RxDBManager.Companion.getInstance(getActivity());
        this.mEvent = RxDBManager.Companion.getInstance(getActivity()).getEventsTable().load(getArguments().getLong(DatabaseActivity.Companion.getPARENT_ID()));
    }

    @Override
    //Build the dialog
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_add_team, null);
        ButterKnife.bind(this, view);

        add_team.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    List<String> strings = Arrays.asList(s.toString().split("\\s*,\\s*"));
                    for (int i = 0; i < strings.size(); i++) {
                        int i1 = Integer.parseInt(strings.get(i));

                        if (i1 > 9999) {
                            throw new RuntimeException(new Exception("cannot import a team greater than 9999"));
                        }
                    }
                } catch (Exception e) {
                    add_team.setErrorEnabled(true);
                    add_team.setError("Double check your formatting");
                    return;
                }

                add_team.setError("");
                add_team.setErrorEnabled(false);
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Team(s)");
        builder.setPositiveButton("Add", this);
        builder.setNegativeButton("Cancel", this);
        builder.setView(view);
        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            Observable.defer(() -> Observable.just(add_team.getEditText().getText().toString()))
                    .map(teamString -> Arrays.asList(teamString.split("\\s*,\\s*")))
                    .map(teamsString -> {
                        List<Integer> teamNumbers = Lists.newArrayList();
                        for (int i = 0; i < teamsString.size(); i++) {
                            int i1 = Integer.parseInt(teamsString.get(i));
                            if (i1 > 9999) {
                                throw new RuntimeException(new Exception("cannot import a team greater than 9999"));
                            }
                            teamNumbers.add(i1);
                        }
                        return Joiner.on(',').join(teamNumbers);
                    })
                    .subscribe(onNext -> {
                        ImportTeamsProgressDialog.newInstance(onNext, mEvent.getId()).show(getFragmentManager(), "importTeamsProgressDialog");
                    }, onError -> {
                        dismiss();
                    });
        } else {
            dismiss();
        }
    }
}
