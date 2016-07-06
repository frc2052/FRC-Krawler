package com.team2052.frckrawler.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Spinner;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.jakewharton.rxbinding.widget.RxAdapterView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.database.metric.MetricHelper;
import com.team2052.frckrawler.database.metric.MetricValue;
import com.team2052.frckrawler.db.Game;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.util.SnackbarUtil;
import com.team2052.frckrawler.views.metric.MetricWidget;
import com.team2052.frckrawler.views.metric.impl.BooleanMetricWidget;
import com.team2052.frckrawler.views.metric.impl.CheckBoxMetricWidget;
import com.team2052.frckrawler.views.metric.impl.ChooserMetricWidget;
import com.team2052.frckrawler.views.metric.impl.CounterMetricWidget;
import com.team2052.frckrawler.views.metric.impl.SliderMetricWidget;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class AddMetricActivity extends DatabaseActivity {
    private static final String TAG = "AddMetricActivity";
    private static String GAME_ID_EXTRA = "AddMetricGameIdExtra";
    private static String METRIC_CATEGORY_EXTRA = "AddMetricMetricCategoryExtra";

    MetricWidget currentWidget;
    CompositeSubscription subscriptions = new CompositeSubscription();

    @BindView(R.id.name)
    TextInputLayout mName;

    @BindView(R.id.type)
    Spinner typeSpinner;

    @BindView(R.id.description)
    TextInputLayout mDescription;

    @BindView(R.id.minimum)
    TextInputLayout mMinimum;

    @BindView(R.id.maximum)
    TextInputLayout mMaximum;

    @BindView(R.id.incrementation)
    TextInputLayout mIncrementation;

    @BindView(R.id.comma_separated_list)
    TextInputLayout mCommaSeparatedList;

    private Game mGame;

    @MetricHelper.MetricCategory
    private int mMetricCategory;

    private Observable<Integer> mMinimumObservable;
    private Observable<Integer> mMaximumObservable;
    private Observable<Integer> mIncrementationObservable;
    private Observable<String> mNameObservable;
    private Observable<String> mDescriptionObservable;
    private Observable<List<String>> mCommaListObservable;
    private Observable<Metric> metricObservable;

    public static Intent newInstance(Context context, long gameId, int metricType) {
        Intent intent = new Intent(context, AddMetricActivity.class);
        intent.putExtra(GAME_ID_EXTRA, gameId);
        intent.putExtra(METRIC_CATEGORY_EXTRA, metricType);
        return intent;
    }

    @Override
    public void inject() {
        getComponent().inject(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGame = dbManager.getGamesTable().load(getIntent().getLongExtra(GAME_ID_EXTRA, 0));

        @MetricHelper.MetricCategory
        int metricCategory = getIntent().getIntExtra(METRIC_CATEGORY_EXTRA, MetricHelper.MATCH_PERF_METRICS);
        this.mMetricCategory = metricCategory;

        mCommaListObservable = Observable.defer(() -> Observable.just(mCommaSeparatedList.getEditText().getText().toString()))
                .map(text -> Strings.isNullOrEmpty(text) ? Lists.newArrayList() : Arrays.asList(text.split("\\s*,\\s*")));
        mDescriptionObservable = Observable.defer(() -> Observable.just(mDescription.getEditText().getText().toString()));
        mNameObservable = Observable.defer(() -> Observable.just(mName.getEditText().getText().toString()))
                .map(text -> Strings.isNullOrEmpty(text) ? getResources().getStringArray(R.array.metric_types)[typeSpinner.getSelectedItemPosition()] : text);
        mIncrementationObservable = Observable.defer(() -> Observable.just(mIncrementation.getEditText().getText().toString()))
                .map(Integer::parseInt)
                .onErrorReturn(ret -> 1);
        mMaximumObservable = Observable.defer(() -> Observable.just(mMaximum.getEditText().getText().toString()))
                .map(Integer::parseInt)
                .onErrorReturn(ret -> 10);
        mMinimumObservable = Observable.defer(() -> Observable.just(mMinimum.getEditText().getText().toString()))
                .map(Integer::parseInt)
                .onErrorReturn(ret -> 0);

        metricObservable = Observable.combineLatest(mNameObservable, mMinimumObservable, mMaximumObservable, mIncrementationObservable, mDescriptionObservable, mCommaListObservable, MetricPreviewParams::new)
                .map(metricPreviewParams -> {
                    MetricHelper.MetricFactory factory = new MetricHelper.MetricFactory(mGame, metricPreviewParams.name);
                    @MetricHelper.MetricType
                    int pos = typeSpinner.getSelectedItemPosition();
                    factory.setMetricType(pos);
                    switch (pos) {
                        case MetricHelper.COUNTER:
                            factory.setDataMinMaxInc(
                                    metricPreviewParams.mMin,
                                    metricPreviewParams.mMax,
                                    metricPreviewParams.mInc);
                            break;
                        case MetricHelper.SLIDER:
                            factory.setDataMinMaxInc(
                                    metricPreviewParams.mMin,
                                    metricPreviewParams.mMax,
                                    null);
                            break;
                        case MetricHelper.CHOOSER:
                        case MetricHelper.CHECK_BOX:
                            factory.setDataListIndexValue(metricPreviewParams.commaList);
                            break;

                        case MetricHelper.BOOLEAN:
                            break;
                    }
                    factory.setDescription(metricPreviewParams.description);
                    factory.setMetricCategory(mMetricCategory);
                    return factory.buildMetric();
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());

        setContentView(R.layout.activity_add_metric);
        ButterKnife.bind(this);

        subscriptions.add(RxAdapterView.itemSelections(typeSpinner)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::changeSelection));
        subscriptions.add(RxTextView.textChanges(mName.getEditText())
                .debounce(250, TimeUnit.MILLISECONDS)
                .subscribe(onNext -> updateMetric()));
        subscriptions.add(RxTextView.textChanges(mIncrementation.getEditText())
                .debounce(250, TimeUnit.MILLISECONDS)
                .subscribe(onNext -> updateMetric()));
        subscriptions.add(RxTextView.textChanges(mMinimum.getEditText())
                .debounce(250, TimeUnit.MILLISECONDS)
                .subscribe(onNext -> updateMetric()));
        subscriptions.add(RxTextView.textChanges(mMaximum.getEditText())
                .debounce(250, TimeUnit.MILLISECONDS)
                .subscribe(onNext -> updateMetric()));
        subscriptions.add(RxTextView.textChanges(mCommaSeparatedList.getEditText())
                .debounce(250, TimeUnit.MILLISECONDS)
                .subscribe(onNext -> updateMetric()));

        mMinimum.getEditText().setText("1");
        mMaximum.getEditText().setText("10");
        mIncrementation.getEditText().setText("1");

        //Setup toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add Metric");
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);

        ViewCompat.setElevation(toolbar, getResources().getDimension(R.dimen.toolbar_elevation));
    }

    private void changeSelection(int position) {
        MetricWidget newWidget;
        switch (position) {
            case MetricHelper.COUNTER:
                mMinimum.setVisibility(View.VISIBLE);
                mMaximum.setVisibility(View.VISIBLE);
                mIncrementation.setVisibility(View.VISIBLE);
                mCommaSeparatedList.setVisibility(View.GONE);
                newWidget = new CounterMetricWidget(this);
                break;
            case MetricHelper.SLIDER:
                mMinimum.setVisibility(View.VISIBLE);
                mMaximum.setVisibility(View.VISIBLE);
                mIncrementation.setVisibility(View.GONE);
                mCommaSeparatedList.setVisibility(View.GONE);
                newWidget = new SliderMetricWidget(this);
                break;
            case MetricHelper.CHOOSER:
            case MetricHelper.CHECK_BOX:
                mMinimum.setVisibility(View.GONE);
                mMaximum.setVisibility(View.GONE);
                mIncrementation.setVisibility(View.GONE);
                mCommaSeparatedList.setVisibility(View.VISIBLE);
                if (position == MetricHelper.CHOOSER) {
                    newWidget = new ChooserMetricWidget(this);
                } else {
                    newWidget = new CheckBoxMetricWidget(this);
                }
                break;
            default:
                mMinimum.setVisibility(View.GONE);
                mMaximum.setVisibility(View.GONE);
                mIncrementation.setVisibility(View.GONE);
                mCommaSeparatedList.setVisibility(View.GONE);
                newWidget = new BooleanMetricWidget(this);
                break;
        }
        setMetricWidget(newWidget);
        updateMetric();
    }

    private void updateMetric() {
        subscriptions.add(metricObservable.map(metric -> new MetricValue(metric, null))
                .subscribe(onNext -> {
                    if (typeSpinner.getSelectedItemPosition() == MetricHelper.CHECK_BOX) {
                        setMetricWidget(new CheckBoxMetricWidget(AddMetricActivity.this, onNext));
                    } else if (currentWidget != null) {
                        currentWidget.setMetricValue(onNext);
                    }
                }, onError -> {
                    Log.e(TAG, "updateMetric: ", onError);
                }));
    }

    private void setMetricWidget(MetricWidget widget) {
        currentWidget = widget;
        ((FrameLayout) findViewById(R.id.metric_preview_container)).removeAllViews();
        ((FrameLayout) findViewById(R.id.metric_preview_container)).addView(currentWidget);
    }

    @Override
    protected void onDestroy() {
        if (!subscriptions.isUnsubscribed()) {
            subscriptions.unsubscribe();
        }
        super.onDestroy();
    }

    @OnClick(R.id.button_save)
    protected void saveButtonClicked(View view) {
        subscriptions.add(metricObservable.subscribe(onNext -> {
            dbManager.getMetricsTable().insert(onNext);

            SnackbarUtil.make(findViewById(R.id.root), "Metric Saved", Snackbar.LENGTH_SHORT).show();
            //Show a snackbar for a second
            subscriptions.add(
                    Observable.defer(() -> Observable.just(null))
                            .delay(1, TimeUnit.SECONDS)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(onNextDelayed -> {
                                finish();
                            }));
        }, onError -> {
            SnackbarUtil.make(findViewById(R.id.root), "Unable to Save Metric", Snackbar.LENGTH_SHORT).show();
        }));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private class MetricPreviewParams {
        String name;
        Integer mMin;
        Integer mMax;
        Integer mInc;
        String description;
        private List<String> commaList;

        MetricPreviewParams(String name, Integer mMin, Integer mMax, Integer mInc, String description, List<String> commaList) {
            this.name = name;
            this.mMin = mMin;
            this.mMax = mMax;
            this.mInc = mInc;
            this.description = description;
            this.commaList = commaList;
        }
    }
}
