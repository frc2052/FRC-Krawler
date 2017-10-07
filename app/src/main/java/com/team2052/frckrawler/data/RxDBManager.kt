package com.team2052.frckrawler.data

import android.content.Context
import com.google.common.base.CaseFormat
import com.google.common.collect.Lists
import com.google.common.collect.Maps
import com.team2052.frckrawler.R
import com.team2052.frckrawler.comparators.MatchNumberComparator
import com.team2052.frckrawler.comparators.RobotTeamNumberComparator
import com.team2052.frckrawler.data.tba.v3.JSON
import com.team2052.frckrawler.helpers.metric.MetricHelper
import com.team2052.frckrawler.models.*
import rx.Observable
import java.util.*
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
        matchesTable.deleteAll()
        pitDataTable.deleteAll()
        seasonsTable.deleteAll()
        matchCommentsTable.deleteAll()
        robotEventsTable.deleteAll()
        matchDataTable.deleteAll()
        metricsTable.deleteAll()
        robotsTable.deleteAll()
        eventsTable.deleteAll()
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

    fun eventsByGame(game_id: Long): Observable<List<Event>> {
        return eventsTable.query(null, game_id).rx().list()
    }

    fun allEvents(): Observable<List<Event>> {
        return eventsTable.query(null, null).rx().list()
    }

    fun robotAtEvents(robot_id: Long): Observable<List<Event>> {
        return robotsTable
                .query(robot_id, null, null)
                .rx()
                .unique()
                .concatMap { robot ->
                    robot.resetRobotEventList()
                    Observable.from(robot.robotEventList)
                }
                .map<Event>(RobotEvent::getEvent)
                .toList()
    }

    fun robotsWithTeam(team_id: Long): Observable<List<Robot>> {

        return robotsTable.query(null, team_id, null).rx().list()
    }

    fun robotsAtEvent(event_id: Long): Observable<List<Robot>> {
        return eventsTable.dao.rx().load(event_id).map { event -> eventsTable.getRobotEvents(event) }.map<List<Robot>> { robotEvents ->
            val robots = Lists.newArrayListWithCapacity<Robot>(robotEvents.size)
            for (i in robotEvents.indices) {
                robots.add(robotEvents[i].robot)
            }
            Collections.sort(robots, RobotTeamNumberComparator())
            robots
        }
    }

    fun allGames(): Observable<List<Season>> {
        return seasonsTable.dao.rx().loadAll()
    }

    fun metricsInGame(season_id: Long, category: Int?): Observable<List<Metric>> {
        val query = metricsTable.queryBuilder.where(MetricDao.Properties.Season_id.eq(season_id))
        if (category != null)
            query.where(MetricDao.Properties.Category.eq(category))

        query.orderDesc(MetricDao.Properties.Priority)
        query.orderAsc(MetricDao.Properties.Id)

        return query.rx().list()
    }

    fun allTeams(): Observable<List<Team>> {
        return teamsTable.dao.rx().loadAll()
    }

    fun matchesAtEvent(event_id: Long): Observable<List<Match>> {
        val query = matchesTable.queryBuilder.where(MatchDao.Properties.Event_id.eq(event_id))
        return query.rx().list()
                .map { matches ->
                    Collections.sort(matches, MatchNumberComparator())
                    matches
                }
    }

    fun gameInfo(mGame: Season?): Observable<out Map<String, String>> {
        return Observable.just(mGame).map<Map<String, String>> { season ->
            val info = Maps.newLinkedHashMap<String, String>()

            if (mGame != null) {
                mGame.resetEventList()
                mGame.resetRobotList()
                val resources = context.resources
                info.put(resources.getString(R.string.game_info_num_of_event), Integer.toString(mGame.eventList.size))
                info.put(resources.getString(R.string.game_info_num_of_robots), Integer.toString(mGame.robotList.size))
                info.put(resources.getString(R.string.game_info_num_of_match_metrics), Integer.toString(metricsTable.getNumberOfMetrics(mGame, MetricHelper.MATCH_PERF_METRICS)))
                info.put(resources.getString(R.string.game_info_num_of_pit_metrics), Integer.toString(metricsTable.getNumberOfMetrics(mGame, MetricHelper.ROBOT_METRICS)))
            }

            info
        }
    }

    fun eventInfo(event_: Event?): Observable<out Map<String, String>> {
        return Observable.just(event_).map<Map<String, String>> { event ->
            val info = Maps.newLinkedHashMap<String, String>()
            val resources = context.resources
            if (event != null) {
                info.put(resources.getString(R.string.event_info_num_of_teams), Integer.toString(eventsTable.getTeamsAtEvent(event).size))
                info.put(resources.getString(R.string.event_info_num_of_matches), Integer.toString(eventsTable.getMatches(event).size))
                info.put(resources.getString(R.string.event_info_num_of_pit_data), Integer.toString(eventsTable.getPitData(event).size))
                info.put(resources.getString(R.string.event_info_num_of_match_data), Integer.toString(eventsTable.getMatchData(event).size))
            }
            info
        }
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
