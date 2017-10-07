package com.team2052.frckrawler.fragments.metric;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.team2052.frckrawler.di.subscribers.KeyValueListSubscriber;
import com.team2052.frckrawler.fragments.ListViewFragment;

import java.util.Map;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MetricInfoFragment extends ListViewFragment<Map<String, String>, KeyValueListSubscriber> {
    public static final String METRIC_ID_ARG = "METRIC_ID";
    private long metricId;

    public static MetricInfoFragment newInstance(long metric_id) {
        MetricInfoFragment metricInfoFragment = new MetricInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(METRIC_ID_ARG, metric_id);
        metricInfoFragment.setArguments(bundle);
        return metricInfoFragment;
    }

    @Override
    public void inject() {
        getMComponent().inject(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        metricId = getArguments().getLong(METRIC_ID_ARG);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListView.setOnItemClickListener((parent, view1, position, id) -> {
            if (id == 0) {
                Observable.just(metricId)
                        .map(metricId -> getRxDbManager().getMetricsTable().load(metricId))
                        .map(metric -> {
                            metric.setEnabled(!metric.getEnabled());
                            metric.update();
                            return metric;
                        })
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(onNext -> refresh(), onError -> {
                            //Handle the exception
                        });
            }
        });
    }

    @Override
    protected Observable<? extends Map<String, String>> getObservable() {
        return getRxDbManager().metricInfo(metricId);
    }
}
