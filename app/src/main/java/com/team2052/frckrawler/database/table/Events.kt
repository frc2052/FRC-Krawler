package com.team2052.frckrawler.database.table

import androidx.room.*

@Entity(tableName = "event_table")
data class Event(

    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "event_id") val id : Int,

    @ColumnInfo(name = "event_name") val name : String,

    @ColumnInfo(name = "event_date") val date : String,

)

@Dao
interface EventsDAO {

    @Query("SELECT * FROM event_table")
    suspend fun getAll(): List<Event>

    @Query("SELECT * FROM event_table WHERE event_date = :date")
    fun getGameByDate(date: String): List<Event>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(event: Event)

    @Query("DELETE FROM event_table")
    suspend fun deleteAll()

}
