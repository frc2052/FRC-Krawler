package com.team2052.frckrawler.metrics.view.impl;

import android.content.Context;
import android.support.v7.widget.AppCompatImageButton;
import android.view.View;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.database.metric.MetricValue;
import com.team2052.frckrawler.metrics.view.MetricWidget;
import com.team2052.frckrawler.util.MetricHelper;
import com.team2052.frckrawler.util.Tuple2;

import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class StopwatchMetricWidget extends MetricWidget {
    private static DecimalFormat decimalFormat = new DecimalFormat("0.0s");

    public long startTime = System.currentTimeMillis();
    public boolean running;
    private double value = 0.0;
    private AppCompatImageButton startResumeButton;
    private Subscription subscription;

    public StopwatchMetricWidget(Context context, MetricValue m) {
        super(context, m);
        setMetricValue(m);
    }

    public StopwatchMetricWidget(Context context) {
        super(context);
    }

    @Override
    public void initViews() {
        startResumeButton = (AppCompatImageButton) findViewById(R.id.start_resume);
        findViewById(R.id.reset).setOnClickListener(v -> reset());
        findViewById(R.id.reset).setVisibility(View.GONE);
        startResumeButton.setOnClickListener(v -> {
            if (!running) {
                startResumeButton.setImageResource(R.drawable.ic_pause_black_48dp);
                start();
            } else {
                startResumeButton.setImageResource(R.drawable.ic_play_arrow_black_48dp);
                stop();
            }
        });
    }

    @Override
    public void setMetricValue(MetricValue m) {
        ((TextView) findViewById(R.id.title)).setText(m.getMetric().getName());
        Tuple2<Double, MetricHelper.ReturnResult> doubleMetricValue = MetricHelper.getDoubleMetricValue(m);
        if (!doubleMetricValue.t2.isError) {
            value = doubleMetricValue.t1;
        } else {
            value = 0.0;
        }
        ((TextView) findViewById(R.id.value)).setText(decimalFormat.format(value));
    }

    private void start() {
        running = true;

        if (value == 0.0) {
            startTime = System.currentTimeMillis();
        } else {
            startTime = (long) (System.currentTimeMillis() - (value * 1000));
        }

        subscription = timerObservable().map(aLong -> {
            updateTime();
            return decimalFormat.format(value);
        }).subscribe(onNext -> {
            ((TextView) findViewById(R.id.value)).setText(onNext);
        }, onError -> {
        });

        findViewById(R.id.reset).setVisibility(View.VISIBLE);
    }

    private void unsubscribe() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    private void updateTime() {
        if (!running)
            return;
        value = (System.currentTimeMillis() - startTime) / 1000.0;
    }

    private void stop() {
        unsubscribe();
        updateTime();
        running = false;
    }

    private void reset() {
        if (!running) {
            findViewById(R.id.reset).setVisibility(View.GONE);
        }
        value = 0.0;
        startTime = System.currentTimeMillis();
        ((TextView) findViewById(R.id.value)).setText(decimalFormat.format(value));
    }

    private Observable<Long> timerObservable() {
        return Observable.timer(100, TimeUnit.MILLISECONDS).repeat().observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    protected void onDetachedFromWindow() {
        unsubscribe();
        super.onDetachedFromWindow();
    }

    @Override
    public JsonElement getData() {
        return MetricHelper.buildNumberMetricValue(value);
    }
}
