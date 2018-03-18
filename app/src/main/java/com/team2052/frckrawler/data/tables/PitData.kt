package com.team2052.frckrawler.data.tables

import com.team2052.frckrawler.data.DBManager
import com.team2052.frckrawler.models.PitDatum
import com.team2052.frckrawler.models.PitDatumDao
import org.greenrobot.greendao.query.QueryBuilder
import java.util.*

class PitData(dao: PitDatumDao, dbManager: DBManager) : AbstractTable<PitDatum, PitDatumDao>(dao, dbManager) {
    fun query(team_id: Long?, metric_id: Long?): QueryBuilder<PitDatum> {
        val queryBuilder = queryBuilder
        if (team_id != null)
            queryBuilder.where(PitDatumDao.Properties.Team_id.eq(team_id))
        if (metric_id != null)
            queryBuilder.where(PitDatumDao.Properties.Metric_id.eq(metric_id))
        return queryBuilder
    }

    fun insertWithSaved(pitDatum: PitDatum): Boolean {
        pitDatum.id = null
        val pitDataQueryBuilder = query(pitDatum.team_id, pitDatum.metric_id)
        val count = pitDataQueryBuilder.count()
        if (count > 0) {
            val unique = pitDataQueryBuilder.unique()
            if (unique.last_updated.time <= System.currentTimeMillis()) {
                unique.last_updated = Date()
                unique.data = pitDatum.data
                dao.update(unique)
            }
            return false
        } else {
            pitDatum.last_updated = Date()
            dao.insert(pitDatum)
            return true
        }
    }
}