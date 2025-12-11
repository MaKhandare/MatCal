package com.itsmatok.matcal.data.calendar.subscriptions

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SubscriptionDao {
    @Query("SELECT * FROM subscriptions")
    suspend fun getAllSubscriptions(): List<CalendarSubscription>

    @Query("SELECT * FROM subscriptions WHERE url = :url LIMIT 1")
    suspend fun getSubscriptionByUrl(url: String): CalendarSubscription?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(subscription: CalendarSubscription)

    @Delete
    suspend fun delete(subscription: CalendarSubscription)
}