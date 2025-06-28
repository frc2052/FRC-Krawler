package com.team2052.frckrawler.data.local.migration

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import android.util.JsonReader
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.team2052.frckrawler.data.local.Event
import com.team2052.frckrawler.data.local.EventDao
import com.team2052.frckrawler.data.local.FRCKrawlerDatabase
import com.team2052.frckrawler.data.local.Game
import com.team2052.frckrawler.data.local.GameDao
import com.team2052.frckrawler.data.local.MetricDao
import com.team2052.frckrawler.data.local.MetricDatum
import com.team2052.frckrawler.data.local.MetricDatumDao
import com.team2052.frckrawler.data.local.MetricDatumGroup
import com.team2052.frckrawler.data.local.MetricRecord
import com.team2052.frckrawler.data.local.MetricSet
import com.team2052.frckrawler.data.local.MetricSetDao
import com.team2052.frckrawler.data.local.MetricType
import com.team2052.frckrawler.data.local.TeamAtEvent
import com.team2052.frckrawler.data.local.TeamAtEventDao
import com.team2052.frckrawler.data.local.init.SeedDatabaseTask
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.StringReader
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.UUID
import javax.inject.Inject

class LegacyDatabaseMigration @Inject constructor(
  @ApplicationContext private val context: Context,
  private val seedDatabaseTask: SeedDatabaseTask,
  private val gameDao: GameDao,
  private val metricSetDao: MetricSetDao,
  private val metricsDao: MetricDao,
  private val metricDatumDao: MetricDatumDao,
  private val eventDao: EventDao,
  private val teamAtEventDao: TeamAtEventDao,
  private val v4Db: FRCKrawlerDatabase,
) {
  companion object {
    private const val LEGACY_DB_NAME = "frc-krawler-database-v3"
  }

  private val moshi: Moshi = Moshi.Builder().build()

  fun requiresMigration(): Boolean {
    val legacyDbFile  = context.getDatabasePath(LEGACY_DB_NAME)
    return legacyDbFile.exists()
  }

  suspend fun migrate() {
    if (!requiresMigration()) return

    withContext(Dispatchers.IO) {
      // Start with a clean slate in case we are trying to recover from a bad migration
      v4Db.clearAllTables()

      // Make sure our reserved game/event/metrics are set up first
      seedDatabaseTask.seed()

      val legacyDb = LegacyDatabaseOpenHelper().readableDatabase

      Timber.d("migrating games")
      val gameIdMap = migrateGames(legacyDb)
      Timber.d("migrating events")
      val eventIdMap = migrateEvents(legacyDb, gameIdMap)
      Timber.d("migrating teams at events")
      val robotIdMap = migrateTeamAtEvent(legacyDb, eventIdMap)
      Timber.d("migrating metrics")
      val metricsInfo = migrateMetrics(legacyDb, gameIdMap)

      Timber.d("migrating match data")
      migrateMatchMetricsData(legacyDb, robotIdMap, eventIdMap, metricsInfo)
      Timber.d("migrating pit data")
      migratePitMetricsData(legacyDb, robotIdMap, eventIdMap, metricsInfo)

      Timber.d("migrating match comments")
      migrateMatchComments(legacyDb, robotIdMap, gameIdMap, eventIdMap, metricsInfo)
      Timber.d("migrating pit comments")
      migratePitComments(legacyDb, gameIdMap, eventIdMap, metricsInfo)

      Timber.d("finished migrating data")
      legacyDb.close()

      context.deleteDatabase(LEGACY_DB_NAME)
    }
  }

  // Returns map of old ID -> new ID
  private suspend fun migrateGames(legacyDb: SQLiteDatabase): MutableMap<Int, MigratedGameMetadata> {
    val idMap = mutableMapOf<Int, MigratedGameMetadata>()
    val games = legacyDb.query(
      "GAME",
      arrayOf(BaseColumns._ID, "NAME"),
      null,
      null,
      null,
      null,
      null
    )
    with(games) {
      while (moveToNext()) {
        val oldId = getInt(getColumnIndexOrThrow(BaseColumns._ID))
        val name = getString(getColumnIndexOrThrow("NAME"))

        val newId = gameDao.insert(
          Game(name = name)
        )

        idMap[oldId] = MigratedGameMetadata(
          newGameId = newId.toInt()
        )
      }
    }

    games.close()
    return idMap
  }

  // Returns map of old ID -> new ID
  private suspend fun migrateEvents(
    legacyDb: SQLiteDatabase,
    gameInfoMap: Map<Int, MigratedGameMetadata>
  ): Map<Int, MigratedEventMetadata> {
    val idMap = mutableMapOf<Int, MigratedEventMetadata>()
    val events = legacyDb.query(
      "EVENT",
      arrayOf(BaseColumns._ID, "FMSID", "NAME", "GAME_ID",),
      null,
      null,
      null,
      null,
      null
    )
    with(events) {
      while (moveToNext()) {
        val oldId = getInt(getColumnIndexOrThrow(BaseColumns._ID))
        val fmsId = getString(getColumnIndexOrThrow("FMSID"))
        val name = getString(getColumnIndexOrThrow("NAME"))
        val oldGameId = getInt(getColumnIndexOrThrow("GAME_ID"))

        val gameInfo = gameInfoMap[oldGameId] ?: continue

        val newId = eventDao.insert(
          Event(
            name = name,
            gameId = gameInfo.newGameId,
            tbaId = fmsId
          )
        )

        idMap[oldId] = MigratedEventMetadata(
          newEventId = newId.toInt(),
          oldGameId = oldGameId,
          gameId = gameInfo.newGameId,
        )
      }
    }

    events.close()
    return idMap
  }

  // Returns map of robot ID -> team number
  private suspend fun migrateTeamAtEvent(
    legacyDb: SQLiteDatabase,
    eventInfoMap: Map<Int, MigratedEventMetadata>,
  ): Map<Int, String> {
    val robotIdMap = mutableMapOf<Int, String>()
    val robotsAtEvent = legacyDb.query(
      "ROBOT_EVENT",
      arrayOf("ROBOT_ID", "EVENT_ID"),
      null,
      null,
      null,
      null,
      null
    )
    with(robotsAtEvent) {
      while (moveToNext()) {
        val robotId = getInt(getColumnIndexOrThrow("ROBOT_ID"))
        val oldEventId = getInt(getColumnIndexOrThrow("EVENT_ID"))

        // Skip robots at this event if the event doesn't exist in the old database
        val eventInfo = eventInfoMap[oldEventId] ?: continue

        val teamNumber = try {
          legacyDb.getTeamNumber(robotId)
        } catch (e: Exception) {
          throw MigrationException(
            phase = "team at event",
            message = "Failed to get team number for robot ID $robotId at event $oldEventId",
            cause = e
          )
        }

        val teamName = try {
          legacyDb.getTeamName(teamNumber)
        } catch (e: Exception) {
          throw MigrationException(
            phase = "team at event",
            message = "Failed to get team name for team $teamNumber at event $oldEventId",
            cause = e
          )
        }

        robotIdMap[robotId] = teamNumber.toString()

        teamAtEventDao.insert(
          TeamAtEvent(
            number = teamNumber.toString(),
            name = teamName,
            eventId = eventInfo.newEventId
          )
        )
      }
    }

    robotsAtEvent.close()
    return robotIdMap
  }

  private fun SQLiteDatabase.getTeamNumber(robotId: Int): Int {
    val robots = query(
      "ROBOT",
      arrayOf("TEAM_ID"),
      "${BaseColumns._ID} = ?",
      arrayOf(robotId.toString()),
      null,
      null,
      null
    )

    robots.moveToFirst()
    val teamNumber = robots.getInt(0)

    robots.close()
    return teamNumber
  }

  private fun SQLiteDatabase.getTeamName(teamNumber: Int): String {
    val teams = query(
      "TEAM",
      arrayOf("NAME"),
      "NUMBER = ?",
      arrayOf(teamNumber.toString()),
      null,
      null,
      null
    )

    teams.moveToFirst()
    val name = teams.getString(0)

    teams.close()
    return name
  }

  // Returns map of old ID -> new MigratedMetricMetadata
  private suspend fun migrateMetrics(
    legacyDb: SQLiteDatabase,
    gameInfoMap: MutableMap<Int, MigratedGameMetadata>
  ): MigratedMetricsInfo {
    val idMap = mutableMapOf<Int, MigratedMetricMetadata>()
    val metrics = legacyDb.query(
      "METRIC",
      arrayOf(BaseColumns._ID, "NAME", "CATEGORY", "TYPE", "DATA", "GAME_ID", "ENABLED"),
      null,
      null,
      null,
      null,
      null
    )
    with(metrics) {
      while (moveToNext()) {
        val oldId = getInt(getColumnIndexOrThrow(BaseColumns._ID))
        val name = getString(getColumnIndexOrThrow("NAME"))
        val category = getInt(getColumnIndexOrThrow("CATEGORY"))
        val type = getInt(getColumnIndexOrThrow("TYPE"))
        val data = getString(getColumnIndexOrThrow("DATA"))
        val oldGameId = getInt(getColumnIndexOrThrow("GAME_ID"))
        val enabled = getInt(getColumnIndexOrThrow("ENABLED"))

        val newGameId = gameInfoMap[oldGameId]!!.newGameId
        val gameMetrics = gameInfoMap[oldGameId] ?: continue
        val metricSetId = when (category) {
          0 -> { // Match metrics
            if (gameMetrics.matchMetricsSetId != null) {
              gameMetrics.matchMetricsSetId
            } else {
              val setId = createMetricSet(newGameId, category)
              gameInfoMap[oldGameId] = gameMetrics.copy(matchMetricsSetId = setId)
              setId
            }
          }
          1 -> { // Pit metrics
            if (gameMetrics.pitMetricsSetId != null) {
              gameMetrics.pitMetricsSetId
            } else {
              val setId = createMetricSet(newGameId, category)
              gameInfoMap[oldGameId] = gameMetrics.copy(pitMetricsSetId = setId)
              setId
            }
          }
          else -> null
        } ?: continue

        val newMetricId = UUID.randomUUID()
        val metricType = legacyTypeToMetricType(type) ?: continue
        val options = try {
          migrateOptions(metricType, data)
        } catch(e: Exception) {
          throw MigrationException(
            phase = "metrics",
            message = "Bad options for metric $oldId, type $metricType: $data",
            cause = e
          )
        }

        metricsDao.insert(
          MetricRecord(
            id = newMetricId.toString(),
            name = name,
            type = metricType,
            priority = oldId,
            enabled = enabled == 1,
            metricSetId = metricSetId,
            options = options
          )
        )

        idMap[oldId] = MigratedMetricMetadata(
          newId = newMetricId.toString(),
          metricSetId = metricSetId,
          type = metricType,
          stringListValues = if (metricType == MetricType.Chooser || metricType == MetricType.Checkbox) {
            options?.split(",") ?: emptyList()
          } else emptyList()
        )
      }
    }

    val commentMetrics = mutableMapOf<Int, String>()
    idMap.values.map { it.metricSetId }
      .toSet()
      .forEach { setId ->
        val commentMetricId = addCommentsMetric(setId)
        commentMetrics[setId] = commentMetricId
      }

    metrics.close()
    return MigratedMetricsInfo(
      migratedMetrics = idMap,
      commentMetricIds = commentMetrics,
    )
  }

  private suspend fun createMetricSet(
    gameId: Int,
    category: Int,
  ): Int {
    val name = when(category) {
      0 -> "Match metrics"
      1 -> "Pit metrics"
      else -> "Metrics"
    }

    val setId = metricSetDao.insert(
      MetricSet(
        name = name,
        gameId = gameId
      )
    )

    val setIdColumnName = when(category) {
      0 -> "matchMetricsSetId"
      1 -> "pitMetricsSetId"
      else -> null
    } ?: return setId.toInt()

    val game = gameDao.get(gameId)
    val gameWithSet = when (category) {
      0 -> game.copy(matchMetricsSetId = setId.toInt())
      else -> game.copy(pitMetricsSetId = setId.toInt())
    }
    gameDao.insert(gameWithSet)

    return setId.toInt()
  }

  private fun legacyTypeToMetricType(type: Int): MetricType? {
    return when (type) {
      0 -> MetricType.Boolean
      1 -> MetricType.Counter
      2 -> MetricType.Slider
      3 -> MetricType.Chooser
      4 -> MetricType.Checkbox
      5 -> MetricType.Stopwatch
      6 -> MetricType.TextField
      else -> null
    }
  }

  private fun migrateOptions(type: MetricType, options: String): String? {
    return when(type) {
      MetricType.Boolean,
        MetricType.Stopwatch,
        MetricType.TextField  -> null
      MetricType.Counter, MetricType.Slider -> {
        val adapter = moshi.adapter(MetricDataRange::class.java)
        val range = adapter.fromJson(options) ?: MetricDataRange(0, 10, 1)
        return "${range.min},${range.max},${range.inc}"
      }
      MetricType.Chooser, MetricType.Checkbox -> {
        val adapter = moshi.adapter(MetricDataValues::class.java)
        val values = adapter.fromJson(options) ?: MetricDataValues(emptyList())
        return values.values.joinToString(separator = ",")
      }
    }
  }

  // Returns new comment metric ID
  private suspend fun addCommentsMetric(
    metricSetId: Int,
  ): String {
    val newMetricId = UUID.randomUUID().toString()
    metricsDao.insert(
      MetricRecord(
        id = newMetricId,
        name = "Comments",
        type = MetricType.TextField,
        priority = Int.MAX_VALUE,
        enabled = true,
        metricSetId = metricSetId,
        options = null
      )
    )
    return newMetricId
  }

  private suspend fun migrateMatchMetricsData(
    legacyDb: SQLiteDatabase,
    robotIdMap: Map<Int, String>,
    eventInfoMap: Map<Int, MigratedEventMetadata>,
    metricsInfo: MigratedMetricsInfo,
  ) {
    val matchData = legacyDb.query(
      "MATCH_DATUM",
      arrayOf("EVENT_ID", "ROBOT_ID", "METRIC_ID", "MATCH_NUMBER", "LAST_UPDATED", "DATA"),
      null,
      null,
      null,
      null,
      null
    )
    with(matchData) {
      while (moveToNext()) {
        val oldEventId = getInt(getColumnIndexOrThrow("EVENT_ID"))
        val robotId = getInt(getColumnIndexOrThrow("ROBOT_ID"))
        val oldMetricId = getInt(getColumnIndexOrThrow("METRIC_ID"))
        val matchNumber = getInt(getColumnIndexOrThrow("MATCH_NUMBER"))
        val data = getString(getColumnIndexOrThrow("DATA"))
        val lastUpdate = getLong(getColumnIndexOrThrow("LAST_UPDATED"))

        val eventId = eventInfoMap[oldEventId]?.newEventId ?: continue
        val teamNumber = robotIdMap[robotId] ?: continue
        val metricId = metricsInfo.migratedMetrics[oldMetricId]?.newId ?: continue
        val value = extractMetricDatumValueString(metricsInfo, oldMetricId, data) ?: continue

        metricDatumDao.insert(
          MetricDatum(
            value = value,
            lastUpdated = ZonedDateTime.ofInstant(
              Instant.ofEpochMilli(lastUpdate),
              ZoneId.systemDefault()
            ),
            group = MetricDatumGroup.Match,
            groupNumber = matchNumber,
            teamNumber = teamNumber,
            metricId = metricId,
            eventId = eventId
          )
          )
      }
    }

    matchData.close()
  }

  private suspend fun migratePitMetricsData(
    legacyDb: SQLiteDatabase,
    robotIdMap: Map<Int, String>,
    eventInfoMap: Map<Int, MigratedEventMetadata>,
    metricsInfo: MigratedMetricsInfo,
  ) {
    val pitData = legacyDb.query(
      "PIT_DATUM",
      arrayOf("EVENT_ID", "ROBOT_ID", "METRIC_ID", "LAST_UPDATED", "DATA"),
      null,
      null,
      null,
      null,
      null
    )
    with(pitData) {
      while (moveToNext()) {
        val oldEventId = getInt(getColumnIndexOrThrow("EVENT_ID"))
        val robotId = getInt(getColumnIndexOrThrow("ROBOT_ID"))
        val oldMetricId = getInt(getColumnIndexOrThrow("METRIC_ID"))
        val data = getString(getColumnIndexOrThrow("DATA"))
        val lastUpdate = getLong(getColumnIndexOrThrow("LAST_UPDATED"))

        val eventId = eventInfoMap[oldEventId]?.newEventId ?: continue
        val teamNumber = robotIdMap[robotId] ?: continue
        val metricId = metricsInfo.migratedMetrics[oldMetricId]?.newId ?: continue
        val value = extractMetricDatumValueString(metricsInfo, oldMetricId, data) ?: continue

        metricDatumDao.insert(
          MetricDatum(
            value = value,
            lastUpdated = ZonedDateTime.ofInstant(
              Instant.ofEpochMilli(lastUpdate),
              ZoneId.systemDefault()
            ),
            group = MetricDatumGroup.Pit,
            groupNumber = 0,
            teamNumber = teamNumber,
            metricId = metricId,
            eventId = eventId
          )
        )
      }
    }

    pitData.close()
  }

  private suspend fun migrateMatchComments(
    legacyDb: SQLiteDatabase,
    robotIdMap: Map<Int, String>,
    gameInfoMap: MutableMap<Int, MigratedGameMetadata>,
    eventInfoMap: Map<Int, MigratedEventMetadata>,
    metricsInfo: MigratedMetricsInfo,
  ) {
    val comments = legacyDb.query(
      "MATCH_COMMENT",
      arrayOf("EVENT_ID", "ROBOT_ID", "MATCH_NUMBER", "LAST_UPDATED", "COMMENT"),
      null,
      null,
      null,
      null,
      null
    )
    with(comments) {
      while (moveToNext()) {
        val oldEventId = getInt(getColumnIndexOrThrow("EVENT_ID"))
        val robotId = getInt(getColumnIndexOrThrow("ROBOT_ID"))
        val matchNumber = getInt(getColumnIndexOrThrow("MATCH_NUMBER"))
        val comment = getString(getColumnIndexOrThrow("COMMENT"))
        val lastUpdate = getLong(getColumnIndexOrThrow("LAST_UPDATED"))

        val eventId = eventInfoMap[oldEventId]?.newEventId ?: continue
        val teamNumber = robotIdMap[robotId] ?: continue

        val oldGameId = eventInfoMap[oldEventId]?.gameId ?: continue
        val gameInfo = gameInfoMap[oldGameId] ?: continue
        val metricId = metricsInfo.commentMetricIds[gameInfo.matchMetricsSetId] ?: continue

        metricDatumDao.insert(
          MetricDatum(
            value = comment,
            lastUpdated = ZonedDateTime.ofInstant(
              Instant.ofEpochMilli(lastUpdate),
              ZoneId.systemDefault()
            ),
            group = MetricDatumGroup.Match,
            groupNumber = matchNumber,
            teamNumber = teamNumber,
            metricId = metricId,
            eventId = eventId
          )
        )
      }
    }

    comments.close()
  }

  private suspend fun migratePitComments(
    legacyDb: SQLiteDatabase,
    gameInfoMap: MutableMap<Int, MigratedGameMetadata>,
    eventInfoMap: Map<Int, MigratedEventMetadata>,
    metricsInfo: MigratedMetricsInfo,
  ) {
    val robotComments = legacyDb.query(
      "ROBOT",
      arrayOf(BaseColumns._ID, "TEAM_ID", "GAME_ID", "COMMENTS", "LAST_UPDATED"),
      null,
      null,
      null,
      null,
      null
    )

    with(robotComments) {
      while (moveToNext()) {
        val robotId = getInt(getColumnIndexOrThrow(BaseColumns._ID))
        val teamNumber = getInt(getColumnIndexOrThrow("TEAM_ID"))
        val oldGameId = getInt(getColumnIndexOrThrow("GAME_ID"))
        val comment = getString(getColumnIndexOrThrow("COMMENTS"))
        val lastUpdate = getLong(getColumnIndexOrThrow("LAST_UPDATED"))

        if (comment.isNullOrEmpty()) {
          continue
        }

        val gameInfo = gameInfoMap[oldGameId] ?: continue
        val metricId = metricsInfo.commentMetricIds[gameInfo.pitMetricsSetId] ?: continue

        // For each event, should copy comment
        val robotEvents = legacyDb.query(
          "ROBOT_EVENT",
          arrayOf("EVENT_ID"),
          "ROBOT_ID = ?",
          arrayOf(robotId.toString()),
          null,
          null,
          null
        )

        while (robotEvents.moveToNext()) {
          val oldEventId = robotEvents.getInt(0)
          val eventId = eventInfoMap[oldEventId]?.newEventId ?: continue

          metricDatumDao.insert(
            MetricDatum(
              value = comment,
              lastUpdated = ZonedDateTime.ofInstant(
                Instant.ofEpochMilli(lastUpdate),
                ZoneId.systemDefault()
              ),
              group = MetricDatumGroup.Match,
              groupNumber = 0,
              teamNumber = teamNumber.toString(),
              metricId = metricId,
              eventId = eventId
            )
          )
        }

        robotEvents.close()

      }
    }

    robotComments.close()
  }

  private fun extractMetricDatumValueString(info: MigratedMetricsInfo, legacyId: Int, data: String): String? {
    val metadata = info.migratedMetrics[legacyId] ?: return null

    try {
      val reader = JsonReader(StringReader(data))
      reader.beginObject()
      reader.nextName() // skip "value" key
      return when (metadata.type) {
        MetricType.Boolean -> {
          reader.nextBoolean().toString()
        }

        MetricType.Counter, MetricType.Slider -> {
          reader.nextInt().toString()
        }

        MetricType.Chooser, MetricType.Checkbox -> {
          reader.beginArray()
          val values = mutableListOf<Int>()
          while (reader.hasNext()) {
            values += reader.nextInt()
          }
          values.joinToString(separator = ",") {
            metadata.stringListValues[it]
          }
        }

        MetricType.Stopwatch -> {
          reader.nextDouble().toString()
        }

        MetricType.TextField -> {
          reader.nextString()
        }
      }
    } catch (e: Exception) {
      throw MigrationException(
        phase = "metric data",
        message = "Failed to parse metric data for legacy ID $legacyId: $data",
        cause = e
      )
    }
  }

  data class MigratedMetricsInfo(
    // Old metric ID -> metadata
    val migratedMetrics: Map<Int, MigratedMetricMetadata>,

    // New metric Set ID -> comment metric ID
    val commentMetricIds: Map<Int, String>,
  )

  data class MigratedMetricMetadata(
    val newId: String,
    val metricSetId: Int,
    val type: MetricType,

    // only used for checkbox and chooser - options
    val stringListValues: List<String> = emptyList()
  )

  data class MigratedEventMetadata(
    val newEventId: Int,
    val oldGameId: Int,
    val gameId: Int,
  )

  data class MigratedGameMetadata(
    val newGameId: Int,
    val matchMetricsSetId: Int? = null,
    val pitMetricsSetId: Int? = null,
  )

  @JsonClass(generateAdapter = true)
  data class MetricDataValues(
    val values: List<String>
  )

  @JsonClass(generateAdapter = true)
  data class MetricDataRange(
    val min: Int = 0,
    val max: Int = 1,
    val inc: Int = 1,
  )

  inner class LegacyDatabaseOpenHelper : SQLiteOpenHelper(context, LEGACY_DB_NAME, null, 7) {
    override fun onCreate(db: SQLiteDatabase?) {}
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {}
  }
}