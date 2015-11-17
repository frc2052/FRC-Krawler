package com.team2052.frckrawler.database;

import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.EventDao;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by root on 11/16/15.
 */
public abstract class ListBinder<T, L> {
    public abstract QueryBuilder<T> getQuery();
}
