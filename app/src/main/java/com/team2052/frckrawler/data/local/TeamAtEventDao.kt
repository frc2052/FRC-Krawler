package com.team2052.frckrawler.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TeamAtEventDao {
    @Delete
    suspend fun delete(teamAtEvent: TeamAtEvent)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(teamAtEvent: TeamAtEvent)

    @Query("SELECT COUNT(number) FROM team_at_event WHERE eventId = :eventId")
    suspend fun getTeamCountAtEvent(eventId: Int): Int

    @Query("SELECT * FROM team_at_event WHERE eventId = :eventId")
    fun getAllTeams(eventId: Int): Flow<List<TeamAtEvent>>
}