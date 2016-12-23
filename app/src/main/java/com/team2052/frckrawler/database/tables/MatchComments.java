package com.team2052.frckrawler.database.tables;

import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.db.MatchComment;
import com.team2052.frckrawler.db.MatchCommentDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.Date;

public class MatchComments extends Table<MatchComment, MatchCommentDao> {
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

    public QueryBuilder<MatchComment> query(Long match_number, Integer game_type, Long robot_id, Long event_id) {
        QueryBuilder<MatchComment> queryBuilder = getQueryBuilder();
        if (match_number != null)
            queryBuilder.where(MatchCommentDao.Properties.Match_number.eq(match_number));
        if (game_type != null)
            queryBuilder.where(MatchCommentDao.Properties.Match_type.eq(game_type));
        if (robot_id != null)
            queryBuilder.where(MatchCommentDao.Properties.Robot_id.eq(robot_id));
        if (event_id != null)
            queryBuilder.where(MatchCommentDao.Properties.Event_id.eq(event_id));
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
