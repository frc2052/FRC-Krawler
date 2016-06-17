package com.team2052.frckrawler.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.jakewharton.rxbinding.widget.RxAdapterView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.database.MetricHelper;
import com.team2052.frckrawler.database.MetricValue;
import com.team2052.frckrawler.db.Game;
import com.team2052.frckrawler.views.ListEditor;
import com.team2052.frckrawler.views.metric.MetricWidget;
import com.team2052.frckrawler.views.metric.impl.BooleanMetricWidget;
import com.team2052.frckrawler.views.metric.impl.CheckBoxMetricWidget;
import com.team2052.frckrawler.views.metric.impl.ChooserMetricWidget;
import com.team2052.frckrawler.views.metric.impl.CounterMetricWidget;
import com.team2052.frckrawler.views.metric.impl.SliderMetricWidget;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class AddMetricActivity extends DatabaseActivity {
    private static final String TAG = "AddMetricActivity";
    private static String GAME_ID_EXTRA = "AddMetricGameIdExtra";
    MetricWidget currentWidget;

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

    @BindView(R.id.list_editor)
    FrameLayout mListEditor;

    @BindView(R.id.list_header)
    TextView mListHeader;

    private ListEditor list;
    private Game mGame;

    private Observable<Integer> mMinimumObservable = Observable.defer(() -> Observable.just(mMinimum.getEditText().getText().toString())).map(Integer::parseInt).onErrorReturn(ret -> 0);
    private Observable<Integer> mMaximumObservable = Observable.defer(() -> Observable.just(mMaximum.getEditText().getText().toString())).map(Integer::parseInt).onErrorReturn(ret -> 10);
    private Observable<Integer> mIncrementationObservable = Observable.defer(() -> Observable.just(mIncrementation.getEditText().getText().toString())).map(Integer::parseInt).onErrorReturn(ret -> 1);
    private Observable<String> mNameObservable = Observable.defer(() -> Observable.just(mName.getEditText().getText().toString())).map(text -> {
        if (Strings.isNullOrEmpty(text)) {
            int selectedItemPosition = typeSpinner.getSelectedItemPosition();
            switch (selectedItemPosition) {
                case MetricHelper.BOOLEAN:
                    return "Boolean";
                case MetricHelper.CHECK_BOX:
                    return "Check Box";
                case MetricHelper.COUNTER:
                    return "Counter";
                case MetricHelper.CHOOSER:
                    return "Chooser";
                case MetricHelper.SLIDER:
                    return "Slider";
            }
        }
        return text;
    });

    public static Intent newInstance(Context context, long gameId) {
        Intent intent = new Intent(context, AddMetricActivity.class);
        intent.putExtra(GAME_ID_EXTRA, gameId);
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

        setContentView(R.layout.activity_add_metric);
        ButterKnife.bind(this);

        RxAdapterView.itemSelections(typeSpinner).debounce(250, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(this::changeSelection);

        RxTextView.textChanges(mName.getEditText()).debounce(500, TimeUnit.MILLISECONDS).subscribe(onNext -> updateMetric());
        RxTextView.textChanges(mIncrementation.getEditText()).debounce(500, TimeUnit.MILLISECONDS).subscribe(onNext -> updateMetric());
        RxTextView.textChanges(mMinimum.getEditText()).debounce(500, TimeUnit.MILLISECONDS).subscribe(onNext -> updateMetric());
        RxTextView.textChanges(mMaximum.getEditText()).debounce(500, TimeUnit.MILLISECONDS).subscribe(onNext -> updateMetric());

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
                mListEditor.removeAllViews();
                mListHeader.setVisibility(View.GONE);
                newWidget = new CounterMetricWidget(this);
                break;
            case MetricHelper.SLIDER:
                mMinimum.setVisibility(View.VISIBLE);
                mMaximum.setVisibility(View.VISIBLE);
                mIncrementation.setVisibility(View.GONE);
                mListEditor.removeAllViews();
                mListHeader.setVisibility(View.GONE);
                newWidget = new SliderMetricWidget(this);
                break;
            case MetricHelper.CHOOSER:
            case MetricHelper.CHECK_BOX:
                mMinimum.setVisibility(View.GONE);
                mMaximum.setVisibility(View.GONE);
                mIncrementation.setVisibility(View.GONE);
                list = new ListEditor(this);
                mListEditor.removeAllViews();
                mListEditor.addView(list);
                mListHeader.setVisibility(View.VISIBLE);
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
                mListEditor.removeAllViews();
                mListHeader.setVisibility(View.GONE);
                newWidget = new BooleanMetricWidget(this);
                break;
        }
        setMetricWidget(newWidget);
        updateMetric();
    }

    public void updateMetric() {
        Observable.combineLatest(mNameObservable, mMinimumObservable, mMaximumObservable, mIncrementationObservable, MetricPreviewParams::new)
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
                            factory.setDataListIndexValue(list.getValues());
                            break;
                    }

                    return new MetricValue(factory.buildMetric(), null);
                }).subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(onNext -> {
            if (typeSpinner.getSelectedItemPosition() == MetricHelper.CHECK_BOX) {
                setMetricWidget(new CheckBoxMetricWidget(AddMetricActivity.this, onNext));
            } else {
                currentWidget.setMetricValue(onNext);
            }
        }, onError -> {
            Log.e(TAG, "updateMetric: ", onError);
        });
    }

    private void setMetricWidget(MetricWidget widget) {
        currentWidget = widget;
        ((FrameLayout) findViewById(R.id.metric_preview_container)).removeAllViews();
        ((FrameLayout) findViewById(R.id.metric_preview_container)).addView(currentWidget);
    }

    private class MetricPreviewParams {
        String name;
        Integer mMin, mMax, mInc;

        MetricPreviewParams(String name, Integer mMin, Integer mMax, Integer mInc) {
            this.name = name;
            this.mMin = mMin;
            this.mMax = mMax;
            this.mInc = mInc;
        }
    }
}
