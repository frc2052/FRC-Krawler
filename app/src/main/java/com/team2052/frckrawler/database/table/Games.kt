package com.team2052.frckrawler.database.table

import androidx.room.*

@Entity(tableName = "game_table")
data class Game(

        @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "game_id") val id : Int,

        @ColumnInfo(name = "game_name") val name : String,

        @ColumnInfo(name = "game_year") val year : Int,

)

@Dao
interface GamesDAO {

    @Query("SELECT * FROM game_table")
    suspend fun getAll(): List<Game>

    @Query("SELECT * FROM game_table WHERE game_year = :year")
    suspend fun getGameByYear(year: Int): List<Game>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(game: Game)

    @Query("DELETE FROM game_table")
    suspend fun deleteAll()

}