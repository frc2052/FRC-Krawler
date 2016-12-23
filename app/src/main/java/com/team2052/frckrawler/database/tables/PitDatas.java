package com.team2052.frckrawler.database.tables;

import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.db.PitData;
import com.team2052.frckrawler.db.PitDataDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.Date;

public class PitDatas extends Table<PitData, PitDataDao> {
    public PitDatas(PitDataDao dao, DBManager dbManager) {
        super(dao, dbManager);
    }

    public boolean insertWithSaved(PitData pitData) {
        pitData.setId(null);
        QueryBuilder<PitData> pitDataQueryBuilder = dao.queryBuilder();
        pitDataQueryBuilder.where(PitDataDao.Properties.Robot_id.eq(pitData.getRobot_id()));
        pitDataQueryBuilder.where(PitDataDao.Properties.Metric_id.eq(pitData.getMetric_id()));
        long count = pitDataQueryBuilder.count();

        if (count > 0) {
            PitData unique = pitDataQueryBuilder.unique();
            if (unique.getLast_updated().getTime() <= System.currentTimeMillis()) {
                unique.setLast_updated(new Date());
                unique.setData(pitData.getData());
                dao.update(unique);
            }
            return false;
        } else {
            pitData.setLast_updated(new Date());
            dao.insert(pitData);
            return true;
        }
    }

    public QueryBuilder<PitData> query(Long robot_id, Long metric_id, Long event_id) {
        QueryBuilder<PitData> queryBuilder = getQueryBuilder();
        if (robot_id != null)
            queryBuilder.where(PitDataDao.Properties.Robot_id.eq(robot_id));
        if (metric_id != null)
            queryBuilder.where(PitDataDao.Properties.Metric_id.eq(metric_id));
        if (event_id != null)
            queryBuilder.where(PitDataDao.Properties.Event_id.eq(event_id));
        return queryBuilder;
    }

    public Metric getMetric(PitData pitData) {
        return dbManager.getMetricsTable().load(pitData.getMetric_id());
    }

    @Override
    public PitData load(long id) {
        return dao.load(id);
    }

    @Override
    public void delete(PitData model) {
        dao.delete(model);
    }

    @Override
    public void insert(PitData model) {
        insertWithSaved(model);
    }
}
