package com.team2052.frckrawler.core.data.tables

import com.team2052.frckrawler.core.common.MetricHelper
import com.team2052.frckrawler.core.data.models.DBManager
import com.team2052.frckrawler.core.data.models.Metric
import com.team2052.frckrawler.core.data.models.MetricDao
import org.greenrobot.greendao.query.QueryBuilder

class Metrics(dao: MetricDao, dbManager: DBManager) : AbstractTable<Metric, MetricDao>(dao, dbManager) {
    fun query(@MetricHelper.MetricCategory category: Int? = null, type: Int? = null, enabled: Boolean? = null): QueryBuilder<Metric> {
        val queryBuilder = queryBuilder
        if (category != null)
            queryBuilder.where(MetricDao.Properties.Category.eq(category))
        if (type != null)
            queryBuilder.where(MetricDao.Properties.Type.eq(type))
        if (enabled != null)
            queryBuilder.where(MetricDao.Properties.Enabled.eq(enabled))
        return queryBuilder
    }

    fun getNumberOfMetrics(metric_category: Int): Int {
        val metricQueryBuilder = dao.queryBuilder()
        metricQueryBuilder.where(MetricDao.Properties.Category.eq(metric_category))
        return metricQueryBuilder.count().toInt()
    }

    override fun delete(model: Metric) {
        model.resetMatchDatumList()
        model.resetPitDatumList()

        dbManager.pitDataTable.dao.deleteInTx(model.getPitDatumList())
        dbManager.matchDataTable.dao.deleteInTx(model.getMatchDatumList())
        super.delete(model)
    }
}