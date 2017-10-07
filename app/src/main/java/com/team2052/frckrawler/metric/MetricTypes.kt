package com.team2052.frckrawler.metric

import android.content.Context
import com.team2052.frckrawler.helpers.metric.MetricHelper
import com.team2052.frckrawler.metric.data.MetricValue
import com.team2052.frckrawler.metric.types.*
import com.team2052.frckrawler.metric.view.MetricWidget
import com.team2052.frckrawler.models.Metric

object MetricTypes {
    private val booleanMetricType = BooleanMetricType()
    private val counterMetricType = CounterMetricType()
    private val sliderMetricType = SliderMetricType()
    private val chooserMetricType = ChooserMetricType()
    private val checkBoxMetricType = CheckBoxMetricType()
    private val stopWatchMetricType = StopWatchMetricType()

    fun createWidget(context: Context, @MetricHelper.MetricType metric_type: Int): MetricWidget? = getType(metric_type).createWidget(context)
    fun createWidget(context: Context, metric: Metric): MetricWidget? = createWidget(context, MetricValue(metric, null))
    fun createWidget(context: Context, metricValue: MetricValue): MetricWidget? = getType(metricValue.metric.type).createWidget(context, metricValue)

    /**
     * Get's the corresponding type value for metric
     * Throws exception if metric type isn't handled or doesn't exist
     */
    fun getType(@MetricHelper.MetricType type: Int): MetricType<*> {
        when (type) {
            MetricHelper.BOOLEAN -> return booleanMetricType
            MetricHelper.COUNTER -> return counterMetricType
            MetricHelper.SLIDER -> return sliderMetricType
            MetricHelper.CHOOSER -> return chooserMetricType
            MetricHelper.CHECK_BOX -> return checkBoxMetricType
            MetricHelper.STOP_WATCH -> return stopWatchMetricType
            else -> throw IllegalStateException("Metric type not found or handled")
        }
    }
}

fun Metric.getTypeInstance(): MetricType<*> = MetricTypes.getType(type)
