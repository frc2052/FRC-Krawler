package com.team2052.frckrawler.database.tables;

import com.team2052.frckrawler.database.DBManager;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

abstract class AbstractTable<T, D extends AbstractDao<T, Long>> {
    protected DBManager dbManager;
    D dao;

    public AbstractTable(D dao, DBManager dbManager) {
        this.dao = dao;
        this.dbManager = dbManager;
    }

    public abstract T load(long id);

    public abstract void delete(T model);

    public void delete(List<T> models) {
        for (int i = 0; i < models.size(); i++) {
            delete(models.get(i));
        }
    }

    public QueryBuilder<T> getQueryBuilder() {
        return dao.queryBuilder();
    }

    public D getDao() {
        return dao;
    }

    public List<T> loadAll() {
        return dao.loadAll();
    }

    public abstract void insert(T model);

    public void deleteAll() {
        dao.deleteAll();
    }
}
