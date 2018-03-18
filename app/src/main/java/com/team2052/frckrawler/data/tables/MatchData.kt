package com.team2052.frckrawler.data.tables

import com.google.common.collect.Lists
import com.google.common.collect.Sets
import com.team2052.frckrawler.data.DBManager
import com.team2052.frckrawler.helpers.metric.MetricHelper
import com.team2052.frckrawler.models.MatchDatum
import com.team2052.frckrawler.models.MatchDatumDao
import org.greenrobot.greendao.query.QueryBuilder
import java.util.*

class MatchData(dao: MatchDatumDao, dbManager: DBManager) : AbstractTable<MatchDatum, MatchDatumDao>(dao, dbManager) {
    fun insertMatchData(matchDatum: MatchDatum): Boolean {
        matchDatum.id = null
        val matchDataQueryBuilder = dao.queryBuilder()
        matchDataQueryBuilder.where(MatchDatumDao.Properties.Team_id.eq(matchDatum.team_id))
        matchDataQueryBuilder.where(MatchDatumDao.Properties.Metric_id.eq(matchDatum.metric_id))
        matchDataQueryBuilder.where(MatchDatumDao.Properties.Match_number.eq(matchDatum.match_number))
        matchDataQueryBuilder.where(MatchDatumDao.Properties.Match_type.eq(matchDatum.match_type))
        val count = matchDataQueryBuilder.count()

        if (count > 0) {
            val unique = matchDataQueryBuilder.unique()
            if (unique.last_updated.time <= matchDatum.last_updated.time) {
                unique.last_updated = Date()
                unique.data = matchDatum.data
                dao.update(unique)
            }
            return false
        } else {
            matchDatum.last_updated = Date()
            dao.insert(matchDatum)
            return true
        }
    }

    fun getMatchNumbersFromMatchData(matchData: List<MatchDatum>): List<Long> {
        val matchNumbers = Sets.newHashSet<Long>()
        matchData.indices.mapTo(matchNumbers) { matchData[it].match_number }
        val listMatchNumbers = Lists.newArrayList(matchNumbers)
        Collections.sort(listMatchNumbers)
        return listMatchNumbers
    }

    fun query(team_id: Long? = null, metric_id: Long? = null, match_number: Long? = null, match_type: Int = MetricHelper.MATCH_GAME_TYPE): QueryBuilder<MatchDatum> {
        val matchDataQueryBuilder = queryBuilder
        if (team_id != null)
            matchDataQueryBuilder.where(MatchDatumDao.Properties.Team_id.eq(team_id))
        if (metric_id != null)
            matchDataQueryBuilder.where(MatchDatumDao.Properties.Metric_id.eq(metric_id))
        if (match_number != null)
            matchDataQueryBuilder.where(MatchDatumDao.Properties.Match_number.eq(match_number))

        matchDataQueryBuilder.where(MatchDatumDao.Properties.Match_type.eq(match_type))
        return matchDataQueryBuilder
    }
}