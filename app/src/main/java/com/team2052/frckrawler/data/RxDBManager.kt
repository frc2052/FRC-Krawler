package com.team2052.frckrawler.data

import android.content.Context
import com.google.common.base.CaseFormat
import com.google.common.collect.Maps
import com.team2052.frckrawler.data.tba.v3.JSON
import com.team2052.frckrawler.models.*
import rx.Observable
import javax.inject.Singleton

@Singleton
/**
 * RX Specific Functions for DB
 * If you want table specific functions with no rx, add them to the respective tables
 */
class RxDBManager private constructor(context: Context) : DBManager(context) {

    fun runInTx(runnable: Runnable) {
        daoSession.runInTx(runnable)
    }

    fun deleteAll() {
        pitDataTable.deleteAll()
        matchCommentsTable.deleteAll()
        matchDataTable.deleteAll()
        metricsTable.deleteAll()
        teamsTable.deleteAll()
    }

    fun teamInfo(team_id: Long): Observable<Map<String, String>> {
        return teamsTable.dao.rx().load(team_id).map<Map<String, String>> { team ->
            val info = Maps.newLinkedHashMap<String, String>()
            info.put("Nickname", team.name)

            val data = JSON.getAsJsonObject(team.data)

            if (data.has("rookie_year") && !data.get("rookie_year").isJsonNull) {
                info.put("Rookie Year", data.get("rookie_year").asString)
            }

            if (data.has("long_name") && !data.get("long_name").isJsonNull) {
                info.put("Name", data.get("long_name").asString)
            }
            info
        }
    }

    fun metricsByCategory(category: Int? = null): Observable<List<Metric>> {
        val query = metricsTable.queryBuilder
        if (category != null) {
            query.where(MetricDao.Properties.Category.eq(category))
        }

        query.orderDesc(MetricDao.Properties.Priority)
        query.orderAsc(MetricDao.Properties.Id)

        return query.rx().list()
    }

    fun allTeams(): Observable<List<Team>> {
        return teamsTable.dao.rx().loadAll()
    }

    fun metricInfo(metricId: Long): Observable<out Map<String, String>> {
        return Observable.just(metricId)
                .map<Metric> { id -> metricsTable.load(id) }
                .map<Map<String, String>> { metric ->
                    val info = Maps.newLinkedHashMap<String, String>()
                    info.put("Enabled", CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, metric.enabled.toString()))
                    //info.put("Priority", String.valueOf(metric.getPriority()));
                    //                    MetricTypeEntry<?> typeEntry = MetricTypeEntryHandler.INSTANCE.getTypeEntry(metric.getType());
                    //                    if (typeEntry != null) {
                    //                        typeEntry.addInfo(metric, info);
                    //                    }
                    info
                }
    }

    val serverLog: Observable<List<ServerLogEntry>>
        get() = Observable.defer {
            val list = serverLogEntries.queryBuilder.orderDesc(ServerLogEntryDao.Properties.Time).list()
            Observable.just(list)
        }

    companion object {
        private var instance: RxDBManager? = null

        @Synchronized
        fun getInstance(context: Context): RxDBManager {
            if (instance == null) {
                instance = RxDBManager(context)
            }
            return instance!!
        }
    }
}
