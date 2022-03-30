package com.team2052.frckrawler.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface GameDao {
    @Delete
    suspend fun delete(singleGame: Game)

    @Insert
    suspend fun insert(singleGame: Game)

    @Query("SELECT * FROM game")
    suspend fun getAll(): List<Game>
}