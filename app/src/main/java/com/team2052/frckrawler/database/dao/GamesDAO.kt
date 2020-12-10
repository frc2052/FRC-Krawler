package com.team2052.frckrawler.database.dao

import androidx.room.*
import com.team2052.frckrawler.database.entity.Game

@Dao
interface GamesDAO {

    @Query("SELECT * FROM games")
    suspend fun getGames(): List<Game>

    @Query("SELECT * FROM games WHERE year = :year")
    suspend fun getGameByYear(year: Int): Game

    @Delete
    suspend fun deleteGame(game: Game)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateGame(game: Game): Long

}