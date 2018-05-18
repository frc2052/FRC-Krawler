package com.team2052.frckrawler.core.data.tables

import com.team2052.frckrawler.core.data.models.DBManager
import org.greenrobot.greendao.AbstractDao
import org.greenrobot.greendao.query.QueryBuilder
import rx.Observable

abstract class AbstractTable<T, D : AbstractDao<T, Long>>(var dao: D, protected var dbManager: DBManager) {
    open fun load(id: Long?): T? = dao.load(id)
    open fun delete(model: T) {
        dao.delete(model)
    }

    open fun insert(model: T): Long {
        return dao.insert(model)
    }

    fun delete(models: List<T>) {
        models.forEach { delete(it) }
    }

    fun loadAll(): List<T> {
        return dao.loadAll()
    }

    fun loadAllObservable(): Observable<List<T>> {
        return Observable.defer { Observable.just(loadAll()) }
    }

    val queryBuilder: QueryBuilder<T>
        get() = dao.queryBuilder()

    fun deleteAll() {
        dao.deleteAll()
    }
}
