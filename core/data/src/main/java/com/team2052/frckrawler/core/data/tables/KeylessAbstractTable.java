package com.team2052.frckrawler.core.data.tables;

import com.team2052.frckrawler.core.data.models.DBManager;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

abstract class KeylessAbstractTable<T, D extends AbstractDao<T, Void>> {
    protected DBManager dbManager;
    D dao;

    public KeylessAbstractTable(D dao, DBManager dbManager) {
        this.dao = dao;
        this.dbManager = dbManager;
    }

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
