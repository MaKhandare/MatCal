package com.itsmatok.matcal.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.itsmatok.matcal.data.calendar.subscriptions.SubscriptionDao
import com.itsmatok.matcal.data.calendar.subscriptions.CalendarSubscription

@Database(entities = [CalendarEvent::class, CalendarSubscription::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class CalendarEventDatabase : RoomDatabase() {
    abstract fun eventDao(): CalendarEventDao
    abstract fun subscriptionDao(): SubscriptionDao

    companion object {
        @Volatile
        private var INSTANCE: CalendarEventDatabase? = null

        fun getDatabase(context: Context): CalendarEventDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CalendarEventDatabase::class.java,
                    "calendar_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}