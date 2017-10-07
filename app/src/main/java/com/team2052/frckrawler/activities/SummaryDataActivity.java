package com.team2052.frckrawler.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.adapters.MetricsStatsAdapter;
import com.team2052.frckrawler.adapters.items.ListItem;
import com.team2052.frckrawler.adapters.items.elements.CompiledMetricListElement;
import com.team2052.frckrawler.metric.data.Compiler;
import com.team2052.frckrawler.models.Event;
import com.team2052.frckrawler.models.Metric;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SummaryDataActivity extends DatabaseActivity {
    public static String EVENT_ID = "EVENT_ID";
    @Inject
    public Compiler mCompiler;
    private ListView mListView;
    private Event mEvent;
    private Metric mMetric;
    private Subscription subscription;

    public static Intent newInstance(Context context, long metric_id, long event_id) {
        Intent intent = new Intent(context, SummaryDataActivity.class);
        intent.putExtra(DatabaseActivity.Companion.getPARENT_ID(), metric_id);
        intent.putExtra(EVENT_ID, event_id);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        mListView = (ListView) findViewById(R.id.list);
        mEvent = getRxDbManager().getEventsTable().load(getIntent().getLongExtra(EVENT_ID, 0));
        mMetric = getRxDbManager().getMetricsTable().load(getIntent().getLongExtra(DatabaseActivity.Companion.getPARENT_ID(), 0));
        setActionBarTitle(getString(R.string.summary_title));
        setActionBarSubtitle(mMetric.getName());

        subscription = mCompiler.getMetricEventSummary(mEvent, mMetric)
                .flatMap(Observable::from)
                .map(compiledMetricValue -> (ListItem) new CompiledMetricListElement(compiledMetricValue))
                .toList()
                .map(compiledMetricListElements -> new MetricsStatsAdapter(SummaryDataActivity.this, mMetric, compiledMetricListElements))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.computation())
                .subscribe(onNext -> {
                    mListView.setAdapter(onNext);
                });
    }

    @Override
    protected void onDestroy() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
        super.onDestroy();
    }

    @Override
    public void inject() {
        getComponent().inject(this);
    }
}
