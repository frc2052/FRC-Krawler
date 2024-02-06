package com.team2052.frckrawler.data.local.migration

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import android.util.JsonReader
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.team2052.frckrawler.data.local.MetricDatumGroup
import com.team2052.frckrawler.data.local.MetricType
import java.io.StringReader
import java.util.UUID

class Migration1to2(
  private val context: Context
) : Migration(1, 2) {
  companion object {
    private const val LEGACY_DB_NAME = "com.team2052.frckrawler.db"
  }

  private val moshi: Moshi = Moshi.Builder().build()

  override fun migrate(db: SupportSQLiteDatabase) {
    // TODO can we handle older versions of the legacy database?
    val legacyDbFile  = context.getDatabasePath(LEGACY_DB_NAME)
    if (!legacyDbFile.exists()) {
      return
    }

    val legacyDb = LegacyDatabaseOpenHelper().readableDatabase
    db.beginTransaction()

    val gameIdMap = migrateGames(legacyDb, db)
    val eventIdMap = migrateEvents(legacyDb, db, gameIdMap)
    val robotIdMap = migrateTeamAtEvent(legacyDb, db, eventIdMap)
    val metricsInfo = migrateMetrics(legacyDb, db, gameIdMap)

    migrateMatchMetricsData(legacyDb, db, robotIdMap, eventIdMap, metricsInfo)
    migratePitMetricsData(legacyDb, db, robotIdMap, eventIdMap, metricsInfo)

    migrateMatchComments(legacyDb, db, robotIdMap, gameIdMap, eventIdMap, metricsInfo)
    migratePitComments(legacyDb, db, gameIdMap, eventIdMap, metricsInfo)

    db.setTransactionSuccessful()
    db.endTransaction()

    context.deleteDatabase(LEGACY_DB_NAME)
  }

  // Returns map of old ID -> new ID
  private fun migrateGames(legacyDb: SQLiteDatabase, db: SupportSQLiteDatabase): MutableMap<Int, MigratedGameMetadata> {
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

        val newId = db.insert(
          table = "Game",
          conflictAlgorithm = SQLiteDatabase.CONFLICT_REPLACE,
          values = ContentValues().apply {
            put("name", name)
          }
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
  private fun migrateEvents(
    legacyDb: SQLiteDatabase,
    db: SupportSQLiteDatabase,
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

        val newId = db.insert(
          table = "Event",
          conflictAlgorithm = SQLiteDatabase.CONFLICT_REPLACE,
          values = ContentValues().apply {
            put("name", name)
            put("gameId", gameInfo.newGameId)
            put("tbaId", fmsId)
          }
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
  private fun migrateTeamAtEvent(
    legacyDb: SQLiteDatabase,
    db: SupportSQLiteDatabase,
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
        val teamNumber = legacyDb.getTeamNumber(robotId)
        val teamName = legacyDb.getTeamName(teamNumber)

        val eventInfo = eventInfoMap[oldEventId] ?: continue

        robotIdMap[robotId] = teamNumber.toString()

        db.insert(
          table = "team_at_event",
          conflictAlgorithm = SQLiteDatabase.CONFLICT_REPLACE,
          values = ContentValues().apply {
            put("number", teamNumber.toString())
            put("name", teamName)
            put("eventId", eventInfo.newEventId)
          }
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
  private fun migrateMetrics(
    legacyDb: SQLiteDatabase,
    db: SupportSQLiteDatabase,
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
        val gameMetrics = gameInfoMap[newGameId] ?: continue
        val metricSetId = when (category) {
          0 -> { // Match metrics
            if (gameMetrics.matchMetricsSetId != null) {
              gameMetrics.matchMetricsSetId
            } else {
              val setId = db.createMetricSet(newGameId, category)
              gameInfoMap[newGameId] = gameMetrics.copy(matchMetricsSetId = setId)
              setId
            }
          }
          1 -> { // Pit metrics
            if (gameMetrics.pitMetricsSetId != null) {
              gameMetrics.pitMetricsSetId
            } else {
              val setId = db.createMetricSet(newGameId, category)
              gameInfoMap[newGameId] = gameMetrics.copy(pitMetricsSetId = setId)
              setId
            }
          }
          else -> null
        } ?: continue

        val newMetricId = UUID.randomUUID()
        val metricType = legacyTypeToMetricType(type) ?: continue
        val options = migrateOptions(metricType, data)
        db.insert(
          table = "metric",
          conflictAlgorithm = SQLiteDatabase.CONFLICT_REPLACE,
          values = ContentValues().apply {
            put("id", newMetricId.toString())
            put("name", name)
            put("type", metricType.name)
            put("priority", oldId.toString()) // just use old ID for initial ordering
            put("enabled", enabled)
            put("metricSetId", metricSetId)
            put("options", options)
          }
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
        val commentMetricId = db.addCommentsMetric(setId)
        commentMetrics[setId] = commentMetricId
      }

    metrics.close()
    return MigratedMetricsInfo(
      migratedMetrics = idMap,
      commentMetricIds = commentMetrics,
    )
  }

  private fun SupportSQLiteDatabase.createMetricSet(
    gameId: Int,
    category: Int,
  ): Int {
    val name = when(category) {
      0 -> "Match metrics"
      1 -> "Pit metrics"
      else -> "Metrics"
    }

    val setId = insert(
      table = "metric_set",
      conflictAlgorithm = SQLiteDatabase.CONFLICT_REPLACE,
      values = ContentValues().apply {
        put("name", name)
        put("gameId", gameId.toString())
      }
    )

    val setIdColumnName = when(category) {
      0 -> "matchMetricsSetId"
      1 -> "pitMetricsSetId"
      else -> null
    } ?: return setId.toInt()

    update(
      "Game",
      conflictAlgorithm = SQLiteDatabase.CONFLICT_REPLACE,
      values = ContentValues().apply {
        put(setIdColumnName, setId.toInt())
      },
      whereClause = "id = ?",
      whereArgs = arrayOf(gameId)
    )

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
  private fun SupportSQLiteDatabase.addCommentsMetric(
    metricSetId: Int,
  ): String {
    val newMetricId = UUID.randomUUID().toString()
    insert(
      table = "metric",
      conflictAlgorithm = SQLiteDatabase.CONFLICT_REPLACE,
      values = ContentValues().apply {
        put("id", newMetricId)
        put("name", "Comments")
        put("type", MetricType.TextField.name)
        put("priority", Int.MAX_VALUE) // just use old ID for initial ordering
        put("enabled", true)
        put("metricSetId", metricSetId)
        put("options", null as String?)
      }
    )
    return newMetricId
  }

  private fun migrateMatchMetricsData(
    legacyDb: SQLiteDatabase,
    db: SupportSQLiteDatabase,
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
        val value = extractMetricDatumValueString(metricsInfo, oldMetricId, data)

        db.insert(
          table = "MetricDatum",
          conflictAlgorithm = SQLiteDatabase.CONFLICT_REPLACE,
          values = ContentValues().apply {
            put("value", value)
            put("lastUpdated", lastUpdate)
            put("[group]", MetricDatumGroup.Match.name)
            put("groupNumber", matchNumber)
            put("teamNumber", teamNumber)
            put("metricId", metricId)
            put("eventId", eventId)
          }
        )
      }
    }

    matchData.close()
  }

  private fun migratePitMetricsData(
    legacyDb: SQLiteDatabase,
    db: SupportSQLiteDatabase,
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
        val value = extractMetricDatumValueString(metricsInfo, oldMetricId, data)

        db.insert(
          table = "MetricDatum",
          conflictAlgorithm = SQLiteDatabase.CONFLICT_REPLACE,
          values = ContentValues().apply {
            put("value", value)
            put("lastUpdated", lastUpdate)
            put("[group]", MetricDatumGroup.Pit.name)
            put("groupNumber", 0)
            put("teamNumber", teamNumber)
            put("metricId", metricId)
            put("eventId", eventId)
          }
        )
      }
    }

    pitData.close()
  }

  private fun migrateMatchComments(
    legacyDb: SQLiteDatabase,
    db: SupportSQLiteDatabase,
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

        db.insert(
          table = "MetricDatum",
          conflictAlgorithm = SQLiteDatabase.CONFLICT_REPLACE,
          values = ContentValues().apply {
            put("value", comment)
            put("lastUpdated", lastUpdate)
            put("[group]", MetricDatumGroup.Match.name)
            put("groupNumber", matchNumber)
            put("teamNumber", teamNumber)
            put("metricId", metricId)
            put("eventId", eventId)
          }
        )
      }
    }

    comments.close()
  }

  private fun migratePitComments(
    legacyDb: SQLiteDatabase,
    db: SupportSQLiteDatabase,
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

          db.insert(
            table = "MetricDatum",
            conflictAlgorithm = SQLiteDatabase.CONFLICT_REPLACE,
            values = ContentValues().apply {
              put("value", comment)
              put("lastUpdated", lastUpdate)
              put("[group]", MetricDatumGroup.Pit.name)
              put("groupNumber", 0)
              put("teamNumber", teamNumber)
              put("metricId", metricId)
              put("eventId", eventId)
            }
          )
        }

        robotEvents.close()

      }
    }

    robotComments.close()
  }

  private fun extractMetricDatumValueString(info: MigratedMetricsInfo, legacyId: Int, data: String): String? {
    val metadata = info.migratedMetrics[legacyId] ?: return null
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
    val min: Int,
    val max: Int,
    val inc: Int,
  )

  inner class LegacyDatabaseOpenHelper : SQLiteOpenHelper(context, LEGACY_DB_NAME, null, 7) {
    override fun onCreate(db: SQLiteDatabase?) {}
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {}
  }
}