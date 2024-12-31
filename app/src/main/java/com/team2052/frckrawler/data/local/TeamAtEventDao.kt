package com.team2052.frckrawler.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface TeamAtEventDao {
  @Delete
  suspend fun delete(teamAtEvent: TeamAtEvent)

  @Query("DELETE FROM team_at_event WHERE eventId = :eventId")
  suspend fun deleteAllFromEvent(eventId: Int)

  @Upsert
  suspend fun insert(teamAtEvent: TeamAtEvent)

  @Insert
  suspend fun insertAll(teams: List<TeamAtEvent>)

  @Query("SELECT COUNT(number) FROM team_at_event WHERE eventId = :eventId")
  fun getTeamCountAtEvent(eventId: Int): Flow<Int>

  @Query("SELECT * FROM team_at_event WHERE eventId = :eventId")
  fun getAllTeams(eventId: Int): Flow<List<TeamAtEvent>>
}