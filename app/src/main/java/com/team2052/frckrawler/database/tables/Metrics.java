package com.team2052.frckrawler.database.tables;

import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.db.Game;
import com.team2052.frckrawler.db.MatchData;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.db.MetricDao;
import com.team2052.frckrawler.db.PitData;
import com.team2052.frckrawler.util.MetricHelper;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

public class Metrics extends Table<Metric, MetricDao> {
    public Metrics(MetricDao dao, DBManager dbManager) {
        super(dao, dbManager);
    }

    public int getNumberOfMetrics(Game game, int metric_category) {
        QueryBuilder<Metric> metricQueryBuilder = dao.queryBuilder();
        metricQueryBuilder.where(MetricDao.Properties.Game_id.eq(game.getId()));
        metricQueryBuilder.where(MetricDao.Properties.Category.eq(metric_category));

        return (int) metricQueryBuilder.count();
    }

    public QueryBuilder<Metric> query(@MetricHelper.MetricCategory Integer category, Integer type, Long game_id, Boolean enabled) {
        QueryBuilder<Metric> queryBuilder = getQueryBuilder();
        if (category != null)
            queryBuilder.where(MetricDao.Properties.Category.eq(category));
        if (type != null)
            queryBuilder.where(MetricDao.Properties.Type.eq(type));
        if (game_id != null)
            queryBuilder.where(MetricDao.Properties.Game_id.eq(game_id));
        if (enabled != null)
            queryBuilder.where(MetricDao.Properties.Enabled.eq(enabled));
        return queryBuilder;
    }

    public void insert(Metric metric) {
        dao.insertOrReplace(metric);
    }

    public List<MatchData> getMatchDataList(Metric metric) {
        metric.resetMatchDataList();
        return metric.getMatchDataList();
    }

    @Override
    public Metric load(long id) {
        return dao.load(id);
    }

    public List<PitData> getPitDataList(Metric metric) {
        metric.resetPitDataList();
        return metric.getPitDataList();
    }

    @Override
    public void delete(Metric metric) {
        metric.resetMatchDataList();
        metric.resetPitDataList();

        dbManager.getPitDataTable().getDao().deleteInTx(metric.getPitDataList());
        dbManager.getMatchDataTable().getDao().deleteInTx(metric.getMatchDataList());
        dao.delete(metric);
    }
}
