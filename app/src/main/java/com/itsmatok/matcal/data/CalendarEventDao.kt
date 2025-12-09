package com.itsmatok.matcal.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CalendarEventDao {
    @Query("SELECT * FROM events")
    fun getAllEvents(): Flow<List<CalendarEvent>>

    @Query("SELECT * FROM events WHERE id = :id")
    fun getEventById(id: Int): Flow<CalendarEvent?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: CalendarEvent): Long

    @Query("DELETE FROM events WHERE id = :id")
    suspend fun deleteEventById(id: Int)
}