package com.itsmatok.matcal.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CalendarEventDao {
    @Query("SELECT * FROM events")
    fun getAllEvents(): Flow<List<CalendarEvent>>

    @Query("SELECT * FROM events WHERE id = :id")
    fun getEventById(id: Int): Flow<CalendarEvent?>

    @Query("SELECT * FROM events WHERE iCalUid = :iCalUid LIMIT 1")
    suspend fun getEventByiCalUid(iCalUid: String): CalendarEvent?

    @Update
    suspend fun updateEvent(event: CalendarEvent)

    @Query("DELETE FROM events WHERE sourceUrl = :url AND icalUid NOT IN (:activeUids)")
    suspend fun deleteOrphanedEvents(url: String, activeUids: List<String>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: CalendarEvent): Long

    @Query("DELETE FROM events WHERE id = :id")
    suspend fun deleteEventById(id: Int)

    @Transaction
    suspend fun syncEvents(url: String, newEvents: List<CalendarEvent>) {
        // 1. Delete classes that were cancelled (not in the new list)
        val activeUids = newEvents.mapNotNull { it.iCalUid }
        if (activeUids.isNotEmpty()) {
            deleteOrphanedEvents(url, activeUids)
        }

        // 2. Insert or Update existing classes
        for (newEvent in newEvents) {
            if (newEvent.iCalUid != null) {
                val existing = getEventByiCalUid(newEvent.iCalUid)
                if (existing != null) {
                    // Keep the local ID, update everything else
                    val updated = newEvent.copy(id = existing.id)
                    updateEvent(updated)
                } else {
                    insertEvent(newEvent)
                }
            } else {
                insertEvent(newEvent)
            }
        }
    }
}