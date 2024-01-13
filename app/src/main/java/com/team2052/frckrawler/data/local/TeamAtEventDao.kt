package com.team2052.frckrawler.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface TeamAtEventDao {
    @Delete
    suspend fun delete(teamAtEvent: TeamAtEvent)

    @Upsert
    suspend fun insert(teamAtEvent: TeamAtEvent)

    @Query("SELECT COUNT(number) FROM team_at_event WHERE eventId = :eventId")
    suspend fun getTeamCountAtEvent(eventId: Int): Int

    @Query("SELECT * FROM team_at_event WHERE eventId = :eventId")
    fun getAllTeams(eventId: Int): Flow<List<TeamAtEvent>>
}