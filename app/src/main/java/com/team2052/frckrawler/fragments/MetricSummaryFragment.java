package com.team2052.frckrawler.fragments;

import android.os.Bundle;

import com.team2052.frckrawler.database.metric.CompileUtil;
import com.team2052.frckrawler.database.metric.Compiler;
import com.team2052.frckrawler.subscribers.KeyValueListSubscriber;

import java.util.Map;

import rx.Observable;

public class MetricSummaryFragment extends ListViewFragment<Map<String, String>, KeyValueListSubscriber> {

    private Compiler mCompiler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCompiler = mComponent.compilerManager();
    }

    @Override
    public void inject() {
        mComponent.inject(this);
    }

    @Override
    protected Observable<? extends Map<String, String>> getObservable() {
        return mCompiler.getMetricEventSummary(null, null)
                .concatMap(Observable::from)
                .map(CompileUtil.mapCompiledMetricValueToKeyValue)
                .toList()
                .map(CompileUtil.mapEntriesToMap);
    }
}
