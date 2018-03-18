package com.team2052.frckrawler.data

import android.content.Context
import com.team2052.frckrawler.data.tables.*
import com.team2052.frckrawler.models.DaoMaster
import com.team2052.frckrawler.models.DaoSession


/**
 * Used to keep a clean database
 * There is no CASCADE ON DELETE function with greenDAO, so we have to configure it manually.

 * @author Adam
 * *
 * @since 10/7/2014
 */
open class DBManager internal constructor(protected var context: Context) {
    protected val daoSession: DaoSession
    private val daoMaster: DaoMaster
    val metricsTable: Metrics
    val matchCommentsTable: MatchComments
    val matchDataTable: MatchData
    val pitDataTable: PitData
    val teamsTable: Teams
    val serverLogEntries: ServerLogEntries

    init {
        val helper = DatabaseHelper(context, DB_NAME, null)

        val db = helper.writableDatabase
        daoMaster = DaoMaster(db)
        daoSession = daoMaster.newSession()

        metricsTable = Metrics(daoSession.metricDao, this)
        matchCommentsTable = MatchComments(daoSession.matchCommentDao, this)
        matchDataTable = MatchData(daoSession.matchDatumDao, this)
        pitDataTable = PitData(daoSession.pitDatumDao, this)
        teamsTable = Teams(daoSession.teamDao, this)
        serverLogEntries = ServerLogEntries(daoSession.serverLogEntryDao, this)
    }

    companion object {
        val DB_NAME = "frc-krawler-database-v4"
    }
}