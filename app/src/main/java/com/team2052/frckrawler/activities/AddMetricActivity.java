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
import com.google.firebase.crash.FirebaseCrash;
import com.jakewharton.rxbinding.widget.RxAdapterView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.database.metric.MetricValue;
import com.team2052.frckrawler.db.Game;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.metric.MetricTypeEntry;
import com.team2052.frckrawler.metric.MetricTypeEntryHandler;
import com.team2052.frckrawler.metrics.view.MetricWidget;
import com.team2052.frckrawler.metrics.view.impl.CheckBoxMetricWidget;
import com.team2052.frckrawler.util.MetricHelper;
import com.team2052.frckrawler.util.SnackbarUtil;

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

    private Observable<Integer> mMinimumObservable, mMaximumObservable, mIncrementationObservable;
    private Observable<String> mNameObservable = Observable.defer(() -> Observable.just(mName.getEditText().getText().toString()))
            .map(text -> Strings.isNullOrEmpty(text) ? getResources().getStringArray(R.array.metric_types)[typeSpinner.getSelectedItemPosition()] : text);
    private Observable<List<String>> mCommaListObservable = Observable.defer(() -> Observable.just(mCommaSeparatedList.getEditText().getText().toString()))
            .map(text -> Strings.isNullOrEmpty(text) ? Lists.newArrayList() : Arrays.asList(text.split("\\s*,\\s*")));
    private Observable<Metric> metricObservable;

    public static Intent newInstance(Context context, long gameId, int metricType) {
        Intent intent = new Intent(context, AddMetricActivity.class);
        intent.putExtra(GAME_ID_EXTRA, gameId);
        intent.putExtra(METRIC_CATEGORY_EXTRA, metricType);
        return intent;
    }

    public static Observable<Integer> createNumberDefaultValueObservable(TextInputLayout textInputLayout, int defaultValue) {
        return Observable.defer(() -> Observable.just(textInputLayout.getEditText().getText().toString()))
                .map(Integer::parseInt)
                .onErrorReturn(ret -> defaultValue);
    }

    @Override
    public void inject() {
        getComponent().inject(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGame = rxDbManager.getGamesTable().load(getIntent().getLongExtra(GAME_ID_EXTRA, 0));
        setStatusBarColor(R.color.amber_700);

        @MetricHelper.MetricCategory
        int metricCategory = getIntent().getIntExtra(METRIC_CATEGORY_EXTRA, MetricHelper.MATCH_PERF_METRICS);
        this.mMetricCategory = metricCategory;

        setContentView(R.layout.activity_add_metric);
        ButterKnife.bind(this);

        mMinimumObservable = createNumberDefaultValueObservable(mMinimum, MetricHelper.MINIMUM_DEFAULT_VALUE);
        mMaximumObservable = createNumberDefaultValueObservable(mMaximum, MetricHelper.MAXIMUM_DEFAULT_VALUE);
        mIncrementationObservable = createNumberDefaultValueObservable(mIncrementation, MetricHelper.INCREMENTATION_DEFAULT_VALUE);

        subscriptions.add(RxAdapterView.itemSelections(typeSpinner)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::changeSelection));

        addObservableToUpdateMetric(RxTextView.textChanges(mName.getEditText()));
        addObservableToUpdateMetric(RxTextView.textChanges(mIncrementation.getEditText()));
        addObservableToUpdateMetric(RxTextView.textChanges(mMinimum.getEditText()));
        addObservableToUpdateMetric(RxTextView.textChanges(mMaximum.getEditText()));
        addObservableToUpdateMetric(RxTextView.textChanges(mCommaSeparatedList.getEditText()));

        mMinimum.getEditText().setText(Integer.toString(MetricHelper.MINIMUM_DEFAULT_VALUE));
        mMaximum.getEditText().setText(Integer.toString(MetricHelper.MAXIMUM_DEFAULT_VALUE));
        mIncrementation.getEditText().setText(Integer.toString(MetricHelper.INCREMENTATION_DEFAULT_VALUE));

        metricObservable = Observable
                .zip(mNameObservable, mMinimumObservable, mMaximumObservable, mIncrementationObservable, mCommaListObservable, MetricPreviewParams::new)
                .map(metricPreviewParams -> {
                    MetricHelper.MetricFactory metricFactory = MetricTypeEntryHandler.INSTANCE.getTypeEntry(typeSpinner.getSelectedItemPosition())
                            .buildMetric(metricPreviewParams.name,
                                    metricPreviewParams.mMin,
                                    metricPreviewParams.mMax,
                                    metricPreviewParams.mInc,
                                    metricPreviewParams.commaList);
                    metricFactory.setMetricCategory(metricCategory);
                    metricFactory.setGameId(mGame.getId());
                    return metricFactory.buildMetric();
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());

        //Setup toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);

        ViewCompat.setElevation(toolbar, getResources().getDimension(R.dimen.toolbar_elevation));
    }

    public void addObservableToUpdateMetric(Observable<?> observable, boolean debounce) {
        if (debounce) {
            observable = observable.debounce(250, TimeUnit.MILLISECONDS);
        }
        subscriptions.add(observable.subscribe(onNext -> updateMetric()));
    }

    public void addObservableToUpdateMetric(Observable<?> observable) {
        addObservableToUpdateMetric(observable, true);
    }

    private void changeSelection(int position) {
        MetricWidget newWidget;

        MetricTypeEntry<?> typeEntry = MetricTypeEntryHandler.INSTANCE.getTypeEntry(position);
        if (typeEntry == null)
            typeEntry = MetricTypeEntryHandler.INSTANCE.getBooleanMetricTypeWidget();

        //noinspection ResourceType
        mMinimum.setVisibility(typeEntry.minimumVisibility());
        //noinspection ResourceType
        mMaximum.setVisibility(typeEntry.maximumVisibility());
        //noinspection ResourceType
        mIncrementation.setVisibility(typeEntry.incrementationVisibility());
        //noinspection ResourceType
        mCommaSeparatedList.setVisibility(typeEntry.commaListVisibility());
        newWidget = typeEntry.getWidget(this);

        setMetricWidget(newWidget);
        updateMetric();
    }

    private void updateMetric() {
        subscriptions.add(
                metricObservable.map(metric -> new MetricValue(metric, null))
                        .subscribe(onNext -> {
                            if (typeSpinner.getSelectedItemPosition() == MetricTypeEntryHandler.INSTANCE.getCheckBoxMetricTypeWidget().getTypeId()) {
                                setMetricWidget(new CheckBoxMetricWidget(AddMetricActivity.this, onNext));
                            } else if (currentWidget != null) {
                                currentWidget.setMetricValue(onNext);
                            }
                        }, onError -> {
                            FirebaseCrash.report(onError);
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
            rxDbManager.getMetricsTable().insert(onNext);

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
            FirebaseCrash.report(onError);
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
        private List<String> commaList;

        MetricPreviewParams(String name, Integer mMin, Integer mMax, Integer mInc, List<String> commaList) {
            this.name = name;
            this.mMin = mMin;
            this.mMax = mMax;
            this.mInc = mInc;
            this.commaList = commaList;
        }
    }
}
