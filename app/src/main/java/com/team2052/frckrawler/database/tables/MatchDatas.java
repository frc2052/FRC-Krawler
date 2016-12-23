package com.team2052.frckrawler.database.tables;

import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.db.MatchData;
import com.team2052.frckrawler.db.MatchDataDao;
import com.team2052.frckrawler.db.Metric;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.Date;

public class MatchDatas extends Table<MatchData, MatchDataDao> {
    public MatchDatas(MatchDataDao dao, DBManager dbManager) {
        super(dao, dbManager);
    }

    public boolean insertMatchData(MatchData matchData) {
        matchData.setId(null);
        QueryBuilder<MatchData> matchDataQueryBuilder = dao.queryBuilder();
        matchDataQueryBuilder.where(MatchDataDao.Properties.Robot_id.eq(matchData.getRobot_id()));
        matchDataQueryBuilder.where(MatchDataDao.Properties.Metric_id.eq(matchData.getMetric_id()));
        matchDataQueryBuilder.where(MatchDataDao.Properties.Match_number.eq(matchData.getMatch_number()));
        matchDataQueryBuilder.where(MatchDataDao.Properties.Event_id.eq(matchData.getEvent_id()));
        matchDataQueryBuilder.where(MatchDataDao.Properties.Match_type.eq(matchData.getMatch_type()));
        long count = matchDataQueryBuilder.count();

        if (count > 0) {
            MatchData unique = matchDataQueryBuilder.unique();
            if (unique.getLast_updated().getTime() <= matchData.getLast_updated().getTime()) {
                unique.setLast_updated(new Date());
                unique.setData(matchData.getData());
                dao.update(unique);
            }
            return false;
        } else {
            matchData.setLast_updated(new Date());
            dao.insert(matchData);
            return true;
        }
    }

    public QueryBuilder<MatchData> query(Long robotId, Long metricId, Long match_number, Integer match_type, Long eventId) {
        QueryBuilder<MatchData> matchDataQueryBuilder = getQueryBuilder();
        if (robotId != null)
            matchDataQueryBuilder.where(MatchDataDao.Properties.Robot_id.eq(robotId));
        if (metricId != null)
            matchDataQueryBuilder.where(MatchDataDao.Properties.Metric_id.eq(metricId));
        if (match_number != null)
            matchDataQueryBuilder.where(MatchDataDao.Properties.Match_number.eq(match_number));
        if (match_type != null)
            matchDataQueryBuilder.where(MatchDataDao.Properties.Match_type.eq(match_type));
        if (eventId != null)
            matchDataQueryBuilder.where(MatchDataDao.Properties.Event_id.eq(eventId));
        return matchDataQueryBuilder;
    }

    public Metric getMetric(MatchData matchData) {
        return dbManager.getMetricsTable().load(matchData.getMetric_id());
    }

    @Override
    public MatchData load(long id) {
        return dao.load(id);
    }

    @Override
    public void delete(MatchData model) {
        dao.delete(model);
    }

    @Override
    public void insert(MatchData model) {
        insertMatchData(model);
    }
}
