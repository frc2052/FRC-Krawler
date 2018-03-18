package com.team2052.frckrawler.fragments.scout;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.firebase.crash.FirebaseCrash;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxAdapterView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.data.tba.v3.JSON;
import com.team2052.frckrawler.helpers.metric.MetricHelper;
import com.team2052.frckrawler.metric.data.MetricValue;
import com.team2052.frckrawler.models.MatchComment;
import com.team2052.frckrawler.models.MatchDatum;
import com.team2052.frckrawler.models.Metric;
import com.team2052.frckrawler.models.MetricDao;
import com.team2052.frckrawler.models.Team;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class ScoutMatchFragment extends BaseScoutFragment {
    private static final String TAG = "ScoutMatchFragment";
    private static String MATCH_TYPE = "MATCH_TYPE";
    @BindView(R.id.match_number_input)
    TextInputLayout mMatchNumberInput;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.add_match_num)
    View addMatchNumButton;
    @BindView(R.id.scroll_view)
    NestedScrollView scrollView;

    private int mMatchType;
    Observable<List<MetricValue>> metricValueObservable = Observable
            .combineLatest(matchNumberObservable(), teamObservable(), MetricValueUpdateParams::new)
            .map(valueParams -> {
                List<MetricValue> metricValues = Lists.newArrayList();
                final QueryBuilder<Metric> metricQueryBuilder = rxDbManager.getMetricsTable().query(MetricHelper.MATCH_PERF_METRICS, null, true)
                        .orderDesc(MetricDao.Properties.Priority)
                        .orderAsc(MetricDao.Properties.Id);
                List<Metric> metrics = metricQueryBuilder.list();
                for (int i = 0; i < metrics.size(); i++) {
                    Metric metric = metrics.get(i);
                    //Query for existing data
                    QueryBuilder<MatchDatum> matchDataQueryBuilder = rxDbManager
                            .getMatchDataTable()
                            .query(valueParams.team.getNumber(), metric.getId(), Long.valueOf(valueParams.match_number), mMatchType);
                    MatchDatum currentData = matchDataQueryBuilder.unique();
                    //Add the metric values
                    metricValues.add(new MetricValue(metric, currentData == null ? null : JSON.getAsJsonObject(currentData.getData())));
                }
                return metricValues;
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());

    Observable<String> metricCommentObservable = Observable
            .combineLatest(matchNumberObservable(), teamObservable(), MetricValueUpdateParams::new)
            .map(valueParams -> {
                final QueryBuilder<MatchComment> matchCommentQueryBuilder = rxDbManager.getMatchCommentsTable().query(Long.valueOf(valueParams.match_number), mMatchType, valueParams.team.getNumber(), 0);
                MatchComment mMatchComment = matchCommentQueryBuilder.unique();
                String comment = null;
                if (mMatchComment != null)
                    comment = mMatchComment.getComment();
                return Strings.nullToEmpty(comment);
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());

    public static ScoutMatchFragment newInstance(int type) {
        ScoutMatchFragment scoutMatchFragment = new ScoutMatchFragment();
        Bundle args = new Bundle();
        args.putInt(MATCH_TYPE, type);
        scoutMatchFragment.setArguments(args);
        return scoutMatchFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMatchType = getArguments().getInt(MATCH_TYPE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scouting_match, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBar supportActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        supportActionBar.setTitle(R.string.match_scout);

        mMatchNumberInput.getEditText().setText("1");

        subscriptions.add(RxView.clicks(addMatchNumButton)
                .doOnNext(aVoid -> mCommentsView.clearFocus())
                .flatMap(aVoid -> matchNumberObservable())
                .map(number -> number += 1)
                .map(String::valueOf)
                .onErrorReturn(throwable -> "")
                .subscribe(numText -> mMatchNumberInput.getEditText().setText(numText)));

        subscriptions.add(RxTextView.afterTextChangeEvents(mMatchNumberInput.getEditText())
                .filter(event -> {
                    try {
                        Integer.parseInt(event.editable().toString());
                        mMatchNumberInput.setErrorEnabled(false);
                        addMatchNumButton.setEnabled(true);
                        mMatchNumberInput.setError("");
                    } catch (NumberFormatException e1) {
                        mMatchNumberInput.setErrorEnabled(true);
                        mMatchNumberInput.setError("Invalid Number");
                        addMatchNumButton.setEnabled(false);
                        return false;
                    }
                    return true;
                }).debounce(500, TimeUnit.MILLISECONDS).subscribe(onNext -> updateMetricValues()));
    }

    private Observable<Integer> matchNumberObservable() {
        return Observable.defer(() -> Observable.just(Integer.parseInt(mMatchNumberInput.getEditText().getText().toString())));
    }

    @Override
    public void updateMetricValues() {
        subscriptions.add(metricValueObservable.subscribe(this::setMetricValues, onError -> {
            //Most likely part of the team observable not being initiated, no big deal
            if (onError instanceof ArrayIndexOutOfBoundsException || onError instanceof NumberFormatException) {
                return;
            }
            onError.printStackTrace();
            FirebaseCrash.log("Match: Error Updating Metric Values");
            FirebaseCrash.report(onError);
        }));
        subscriptions.add(metricCommentObservable.subscribe(RxTextView.text(mCommentsView.getEditText()), onError -> {
            //Most likely part of the team observable not being initiated, no big deal
            if (onError instanceof ArrayIndexOutOfBoundsException || onError instanceof NumberFormatException) {
                return;
            }
            onError.printStackTrace();
            FirebaseCrash.log("Match: Error Updating Comments");
            FirebaseCrash.report(onError);
        }));
    }

    @Override
    public Observable<Boolean> getSaveMetricObservable() {
        return Observable.combineLatest(matchNumberObservable(), teamObservable(), Observable.defer(() -> Observable.just(getValues())), Observable.just(mCommentsView.getEditText().getText().toString()), MatchScoutSaveMetric::new)
                .map(matchScoutSaveMetric -> {
                    //Insert Metric Data
                    boolean saved = false;
                    for (MetricValue metricValue : matchScoutSaveMetric.metricValues) {
                        MatchDatum matchDatum = new MatchDatum(
                                null,
                                matchScoutSaveMetric.team.getNumber(),
                                metricValue.getMetric().getId(),
                                mMatchType,
                                matchScoutSaveMetric.matchNum,
                                new Date(),
                                metricValue.valueAsString());
                        if (rxDbManager.getMatchDataTable().insertMatchData(matchDatum) && !saved) {
                            saved = true;
                        }
                    }


                    if (!Strings.isNullOrEmpty(matchScoutSaveMetric.comment)) {
                        MatchComment matchComment = new MatchComment(null);
                        matchComment.setMatch_number((long) matchScoutSaveMetric.matchNum);
                        matchComment.setMatch_type(mMatchType);
                        matchComment.setTeam(matchScoutSaveMetric.team);
                        matchComment.setComment(matchScoutSaveMetric.comment);
                        if (rxDbManager.getMatchCommentsTable().insertMatchComment(matchComment) && !saved)
                            saved = true;
                    }
                    return saved;
                });
    }

    private int getMatchNumber() {
        try {
            return Integer.parseInt(mMatchNumberInput.getEditText().getText().toString());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    @Override
    protected void saveMetrics(View viewClicked) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(getContext());
        builder.title("Confirm Save");
        builder.autoDismiss(false);
        builder.positiveText("Save");
        builder.negativeText("Cancel");
        builder.customView(R.layout.confirm_save_dialog, false);
        builder.onPositive((dialog, which) -> {
            super.saveMetrics(viewClicked);
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(dialog.findViewById(R.id.confirm_save_dialog_match).getWindowToken(), 0);
            dialog.dismiss();
        });
        builder.onNegative((dialog, which) -> dialog.dismiss());

        MaterialDialog build = builder.build();
        build.show();


        Spinner robot_spinner = build.getView().findViewById(R.id.confirm_save_dialog_team);
        robot_spinner.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, robotNames));
        TextInputLayout match_number = build.getView().findViewById(R.id.confirm_save_dialog_match);

        Observable<Integer> match_number_observable = RxTextView.afterTextChangeEvents(match_number.getEditText())
                .map(event -> {
                    try {
                        return Integer.parseInt(event.editable().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return -1;
                })
                .debounce(500, TimeUnit.MILLISECONDS);

        Observable<Team> team_confirm_dialog_observable = RxAdapterView.itemSelections(robot_spinner).map(teams::get);
        Observable.combineLatest(match_number_observable, team_confirm_dialog_observable, MetricValueUpdateParams::new)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNext -> {
                    if (onNext.match_number == getMatchNumber() && onNext.team.equals(getSelectedTeam())) {
                        build.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                    } else {
                        build.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                    }
                }, onError -> {
                    build.dismiss();
                    build.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                    super.saveMetrics(viewClicked);
                }, () -> {
                });
    }

    private static class MetricValueUpdateParams {
        Integer match_number;
        Team team;

        public MetricValueUpdateParams(Integer match_number, Team team) {
            this.match_number = match_number;
            this.team = team;
        }
    }

    public class MatchScoutSaveMetric {
        Integer matchNum;
        Team team;
        List<MetricValue> metricValues;
        String comment;

        public MatchScoutSaveMetric(Integer matchNum, Team team, List<MetricValue> metricValues, String comment) {
            this.matchNum = matchNum;
            this.team = team;
            this.metricValues = metricValues;
            this.comment = comment;
        }
    }


}
