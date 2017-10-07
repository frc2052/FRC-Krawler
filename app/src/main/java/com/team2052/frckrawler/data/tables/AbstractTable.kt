package com.team2052.frckrawler.data.tables

import com.team2052.frckrawler.data.DBManager
import org.greenrobot.greendao.AbstractDao
import org.greenrobot.greendao.query.QueryBuilder

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

    val queryBuilder: QueryBuilder<T>
        get() = dao.queryBuilder()

    fun deleteAll() {
        dao.deleteAll()
    }
}
