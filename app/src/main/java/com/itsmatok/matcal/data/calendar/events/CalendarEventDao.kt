package com.itsmatok.matcal.data.calendar.events

import androidx.room.Dao
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

    @Query("SELECT * FROM events WHERE sourceUrl = :url")
    suspend fun getEventsByUrl(url: String): List<CalendarEvent>

    @Query("SELECT * FROM events WHERE id = :id")
    fun getEventById(id: Int): Flow<CalendarEvent?>

    @Query("SELECT * FROM events WHERE iCalUid = :iCalUid LIMIT 1")
    suspend fun getEventByiCalUid(iCalUid: String): CalendarEvent?

    @Update
    suspend fun updateEvent(event: CalendarEvent)

    @Update
    suspend fun updateAll(events: List<CalendarEvent>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: CalendarEvent): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(events: List<CalendarEvent>)

    @Query("DELETE FROM events WHERE id = :id")
    suspend fun deleteEventById(id: Int)

    @Query("DELETE FROM events WHERE sourceUrl = :url AND icalUid NOT IN (:activeUids)")
    suspend fun deleteOrphanedEvents(url: String, activeUids: List<String>)

    @Query("DELETE FROM events WHERE sourceUrl = :url")
    suspend fun deleteEventsBySource(url: String)

    @Query("DELETE FROM events WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<Int>)

    @Transaction
    suspend fun syncEvents(url: String, newEvents: List<CalendarEvent>) {
        val existingEvents = getEventsByUrl(url)

        val existingMap = existingEvents
            .filter { it.iCalUid != null }
            .associateBy { it.iCalUid }

        val toInsert = ArrayList<CalendarEvent>()
        val toUpdate = ArrayList<CalendarEvent>()

        val processedUids = HashSet<String>()

        for (newEvent in newEvents) {
            val uid = newEvent.iCalUid

            if (uid != null) {
                processedUids.add(uid)
                val existing = existingMap[uid]

                if (existing != null) {
                    toUpdate.add(newEvent.copy(id = existing.id))
                } else {
                    toInsert.add(newEvent)
                }
            } else {
                toInsert.add(newEvent)
            }
        }

        val eventsToDelete = existingEvents.filter {
            it.iCalUid != null && !processedUids.contains(it.iCalUid)
        }
        val idsToDelete = eventsToDelete.map { it.id }

        if (toInsert.isNotEmpty()) insertAll(toInsert)
        if (toUpdate.isNotEmpty()) updateAll(toUpdate)
        if (idsToDelete.isNotEmpty()) deleteByIds(idsToDelete)
    }
}