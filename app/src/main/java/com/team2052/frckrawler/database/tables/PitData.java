package com.team2052.frckrawler.database.tables;

import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.db.PitDatum;
import com.team2052.frckrawler.db.PitDatumDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.Date;

public class PitData extends Table<PitDatum, PitDatumDao> {
    public PitData(PitDatumDao dao, DBManager dbManager) {
        super(dao, dbManager);
    }

    public boolean insertWithSaved(PitDatum pitDatum) {
        pitDatum.setId(null);
        QueryBuilder<PitDatum> pitDataQueryBuilder = dao.queryBuilder();
        pitDataQueryBuilder.where(PitDatumDao.Properties.Robot_id.eq(pitDatum.getRobot_id()));
        pitDataQueryBuilder.where(PitDatumDao.Properties.Metric_id.eq(pitDatum.getMetric_id()));
        long count = pitDataQueryBuilder.count();

        if (count > 0) {
            PitDatum unique = pitDataQueryBuilder.unique();
            if (unique.getLast_updated().getTime() <= System.currentTimeMillis()) {
                unique.setLast_updated(new Date());
                unique.setData(pitDatum.getData());
                dao.update(unique);
            }
            return false;
        } else {
            pitDatum.setLast_updated(new Date());
            dao.insert(pitDatum);
            return true;
        }
    }

    public QueryBuilder<PitDatum> query(Long robot_id, Long metric_id, Long event_id) {
        QueryBuilder<PitDatum> queryBuilder = getQueryBuilder();
        if (robot_id != null)
            queryBuilder.where(PitDatumDao.Properties.Robot_id.eq(robot_id));
        if (metric_id != null)
            queryBuilder.where(PitDatumDao.Properties.Metric_id.eq(metric_id));
        if (event_id != null)
            queryBuilder.where(PitDatumDao.Properties.Event_id.eq(event_id));
        return queryBuilder;
    }

    public Metric getMetric(PitDatum pitDatum) {
        return dbManager.getMetricsTable().load(pitDatum.getMetric_id());
    }

    @Override
    public PitDatum load(long id) {
        return dao.load(id);
    }

    @Override
    public void delete(PitDatum model) {
        dao.delete(model);
    }

    @Override
    public void insert(PitDatum model) {
        insertWithSaved(model);
    }
}
