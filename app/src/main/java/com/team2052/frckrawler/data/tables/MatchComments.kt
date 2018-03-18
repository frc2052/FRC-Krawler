package com.team2052.frckrawler.data.tables

import com.team2052.frckrawler.data.DBManager
import com.team2052.frckrawler.helpers.metric.MetricHelper
import com.team2052.frckrawler.models.MatchComment
import com.team2052.frckrawler.models.MatchCommentDao
import org.greenrobot.greendao.query.QueryBuilder
import java.util.*

class MatchComments(dao: MatchCommentDao, dbManager: DBManager) : AbstractTable<MatchComment, MatchCommentDao>(dao, dbManager) {
    fun insertMatchComment(matchComment: MatchComment): Boolean {
        matchComment.id = null
        val matchCommentQueryBuilder = dao.queryBuilder()
        matchCommentQueryBuilder.where(MatchCommentDao.Properties.Team_id.eq(matchComment.team_id))
        matchCommentQueryBuilder.where(MatchCommentDao.Properties.Match_number.eq(matchComment.match_number))
        matchCommentQueryBuilder.where(MatchCommentDao.Properties.Match_type.eq(matchComment.match_type))
        val count = matchCommentQueryBuilder.count()
        if (count > 0) {
            val currentData = matchCommentQueryBuilder.unique()
            if (currentData.last_updated.time <= System.currentTimeMillis()) {
                currentData.last_updated = Date()
                currentData.comment = matchComment.comment
                dao.update(currentData)
            }
            return false
        } else {
            matchComment.last_updated = Date()
            dao.insert(matchComment)
            return true
        }
    }

    fun query(match_number: Long?, game_type: Int?, team_id: Long?, match_type: Long = MetricHelper.MATCH_GAME_TYPE.toLong()): QueryBuilder<MatchComment> {
        val queryBuilder = queryBuilder
        if (match_number != null)
            queryBuilder.where(MatchCommentDao.Properties.Match_number.eq(match_number))
        if (game_type != null)
            queryBuilder.where(MatchCommentDao.Properties.Match_type.eq(game_type))
        if (team_id != null)
            queryBuilder.where(MatchCommentDao.Properties.Team_id.eq(team_id))

        queryBuilder.where(MatchCommentDao.Properties.Match_type.eq(MetricHelper.MATCH_GAME_TYPE))

        return queryBuilder
    }
}