package com.team2052.frckrawler.core.metrics.view.impl

import android.content.Context
import android.support.v7.widget.AppCompatImageButton
import android.view.View
import android.widget.TextView
import com.google.gson.JsonElement
import com.team2052.frckrawler.core.metrics.MetricDataHelper
import com.team2052.frckrawler.core.metrics.R
import com.team2052.frckrawler.core.metrics.data.MetricValue
import com.team2052.frckrawler.core.metrics.view.MetricWidget
import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit

class StopwatchMetricWidget : MetricWidget {

    var startTime = System.currentTimeMillis()
    var running: Boolean = false
    private var value = 0.0
    private var startResumeButton: AppCompatImageButton? = null
    private var subscription: Subscription? = null

    constructor(context: Context, m: MetricValue) : super(context, m) {
        setMetricValue(m)
    }

    constructor(context: Context) : super(context) {}

    override fun initViews() {
        inflater.inflate(R.layout.widget_metric_stopwatch, this)
        startResumeButton = findViewById<View>(R.id.start_resume) as AppCompatImageButton
        findViewById<View>(R.id.reset).setOnClickListener { v -> reset() }
        findViewById<View>(R.id.reset).visibility = View.GONE
        startResumeButton!!.setOnClickListener { v ->
            if (!running) {
                startResumeButton!!.setImageResource(R.drawable.ic_pause_black_48dp)
                start()
            } else {
                startResumeButton!!.setImageResource(R.drawable.ic_play_arrow_black_48dp)
                stop()
            }
        }
    }

    override fun setMetricValue(m: MetricValue) {
        (findViewById<View>(R.id.title) as TextView).text = m.metric.name
        val doubleMetricValue = MetricDataHelper.getDoubleMetricValue(m)
        if (!doubleMetricValue.t2.isError) {
            value = doubleMetricValue.t1
        } else {
            value = 0.0
        }
        (findViewById<View>(R.id.value) as TextView).text = decimalFormat.format(value)
    }

    private fun start() {
        running = true

        if (value == 0.0) {
            startTime = System.currentTimeMillis()
        } else {
            startTime = (System.currentTimeMillis() - value * 1000).toLong()
        }

        subscription = timerObservable().map { aLong ->
            updateTime()
            decimalFormat.format(value)
        }.subscribe({ onNext -> (findViewById<View>(R.id.value) as TextView).text = onNext }) { onError -> }

        findViewById<View>(R.id.reset).visibility = View.VISIBLE
    }

    private fun unsubscribe() {
        if (subscription != null && !subscription!!.isUnsubscribed) {
            subscription!!.unsubscribe()
        }
    }

    private fun updateTime() {
        if (!running)
            return
        value = (System.currentTimeMillis() - startTime) / 1000.0
    }

    private fun stop() {
        unsubscribe()
        updateTime()
        running = false
    }

    private fun reset() {
        if (!running) {
            findViewById<View>(R.id.reset).visibility = View.GONE
        }
        value = 0.0
        startTime = System.currentTimeMillis()
        (findViewById<View>(R.id.value) as TextView).text = decimalFormat.format(value)
    }

    private fun timerObservable(): Observable<Long> {
        return Observable.timer(100, TimeUnit.MILLISECONDS).repeat().observeOn(AndroidSchedulers.mainThread())
    }

    override fun onDetachedFromWindow() {
        unsubscribe()
        super.onDetachedFromWindow()
    }

    override val data: JsonElement get() = MetricDataHelper.buildNumberMetricValue(value)

    companion object {
        private val decimalFormat = DecimalFormat("0.0s")
    }
}
