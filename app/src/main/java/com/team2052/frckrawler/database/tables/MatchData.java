package com.team2052.frckrawler.database.tables;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.db.MatchDatum;
import com.team2052.frckrawler.db.MatchDatumDao;
import com.team2052.frckrawler.db.Metric;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import rx.functions.Func1;

public class MatchData extends AbstractTable<MatchDatum, MatchDatumDao> {
    public List<Long> getMatchNumbersFromMatchData(List<MatchDatum> matchData){
        Set<Long> matchNumbers = Sets.newHashSet();
        for (int i = 0; i < matchData.size(); i++) {
            matchNumbers.add(matchData.get(i).getMatch_number());
        }
        ArrayList<Long> listMatchNumbers = Lists.newArrayList(matchNumbers);
        Collections.sort(listMatchNumbers);
        return listMatchNumbers;
    }

    public MatchData(MatchDatumDao dao, DBManager dbManager) {
        super(dao, dbManager);
    }

    public boolean insertMatchData(MatchDatum matchDatum) {
        matchDatum.setId(null);
        QueryBuilder<MatchDatum> matchDataQueryBuilder = dao.queryBuilder();
        matchDataQueryBuilder.where(MatchDatumDao.Properties.Robot_id.eq(matchDatum.getRobot_id()));
        matchDataQueryBuilder.where(MatchDatumDao.Properties.Metric_id.eq(matchDatum.getMetric_id()));
        matchDataQueryBuilder.where(MatchDatumDao.Properties.Match_number.eq(matchDatum.getMatch_number()));
        matchDataQueryBuilder.where(MatchDatumDao.Properties.Event_id.eq(matchDatum.getEvent_id()));
        matchDataQueryBuilder.where(MatchDatumDao.Properties.Match_type.eq(matchDatum.getMatch_type()));
        long count = matchDataQueryBuilder.count();

        if (count > 0) {
            MatchDatum unique = matchDataQueryBuilder.unique();
            if (unique.getLast_updated().getTime() <= matchDatum.getLast_updated().getTime()) {
                unique.setLast_updated(new Date());
                unique.setData(matchDatum.getData());
                dao.update(unique);
            }
            return false;
        } else {
            matchDatum.setLast_updated(new Date());
            dao.insert(matchDatum);
            return true;
        }
    }

    public QueryBuilder<MatchDatum> query(Long robotId, Long metricId, Long match_number, Integer match_type, Long eventId) {
        QueryBuilder<MatchDatum> matchDataQueryBuilder = getQueryBuilder();
        if (robotId != null)
            matchDataQueryBuilder.where(MatchDatumDao.Properties.Robot_id.eq(robotId));
        if (metricId != null)
            matchDataQueryBuilder.where(MatchDatumDao.Properties.Metric_id.eq(metricId));
        if (match_number != null)
            matchDataQueryBuilder.where(MatchDatumDao.Properties.Match_number.eq(match_number));
        if (match_type != null) {
            matchDataQueryBuilder.where(MatchDatumDao.Properties.Match_type.eq(match_type));
        } else {
            // Default to normal match data
            matchDataQueryBuilder.where(MatchDatumDao.Properties.Match_type.eq(0));
        }
        if (eventId != null)
            matchDataQueryBuilder.where(MatchDatumDao.Properties.Event_id.eq(eventId));
        return matchDataQueryBuilder;
    }

    public Metric getMetric(MatchDatum matchDatum) {
        return dbManager.getMetricsTable().load(matchDatum.getMetric_id());
    }

    @Override
    public MatchDatum load(long id) {
        return dao.load(id);
    }

    @Override
    public void delete(MatchDatum model) {
        dao.delete(model);
    }

    @Override
    public void insert(MatchDatum model) {
        insertMatchData(model);
    }
}
