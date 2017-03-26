package com.team2052.frckrawler.database.tables;

import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.db.MatchComment;
import com.team2052.frckrawler.db.MatchCommentDao;
import com.team2052.frckrawler.util.MetricHelper;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.Date;

public class MatchComments extends AbstractTable<MatchComment, MatchCommentDao> {
    public MatchComments(MatchCommentDao dao, DBManager dbManager) {
        super(dao, dbManager);
    }

    public boolean insertMatchComment(MatchComment matchComment) {
        matchComment.setId(null);
        QueryBuilder<MatchComment> matchCommentQueryBuilder = dao.queryBuilder();
        matchCommentQueryBuilder.where(MatchCommentDao.Properties.Event_id.eq(matchComment.getEvent_id()));
        matchCommentQueryBuilder.where(MatchCommentDao.Properties.Robot_id.eq(matchComment.getRobot_id()));
        matchCommentQueryBuilder.where(MatchCommentDao.Properties.Match_number.eq(matchComment.getMatch_number()));
        matchCommentQueryBuilder.where(MatchCommentDao.Properties.Match_type.eq(matchComment.getMatch_type()));
        long count = matchCommentQueryBuilder.count();
        if (count > 0) {
            MatchComment currentData = matchCommentQueryBuilder.unique();
            if (currentData.getLast_updated().getTime() <= System.currentTimeMillis()) {
                currentData.setLast_updated(new Date());
                currentData.setComment(matchComment.getComment());
                dao.update(currentData);
            }
            return false;
        } else {
            matchComment.setLast_updated(new Date());
            dao.insert(matchComment);
            return true;
        }
    }

    public QueryBuilder<MatchComment> query(Long match_number, Integer game_type, Long robot_id, Long event_id, Long match_type) {
        QueryBuilder<MatchComment> queryBuilder = getQueryBuilder();
        if (match_number != null)
            queryBuilder.where(MatchCommentDao.Properties.Match_number.eq(match_number));
        if (game_type != null) {
            queryBuilder.where(MatchCommentDao.Properties.Match_type.eq(game_type));
        }
        if (robot_id != null)
            queryBuilder.where(MatchCommentDao.Properties.Robot_id.eq(robot_id));
        if (event_id != null)
            queryBuilder.where(MatchCommentDao.Properties.Event_id.eq(event_id));

        //Default to match game type
        if (match_type != null) {
            queryBuilder.where(MatchCommentDao.Properties.Match_type.eq(match_type));
        } else {
            queryBuilder.where(MatchCommentDao.Properties.Match_type.eq(MetricHelper.MATCH_GAME_TYPE));
        }

        return queryBuilder;
    }

    @Override
    public MatchComment load(long id) {
        return dao.load(id);
    }


    @Override
    public void delete(MatchComment matchComment) {
        dao.delete(matchComment);
    }

    @Override
    public void insert(MatchComment model) {
        insertMatchComment(model);
    }
}
