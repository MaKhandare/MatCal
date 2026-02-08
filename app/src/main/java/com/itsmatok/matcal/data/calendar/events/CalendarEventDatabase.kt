package com.itsmatok.matcal.data.calendar.events

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.itsmatok.matcal.data.calendar.subscriptions.SubscriptionDao
import com.itsmatok.matcal.data.calendar.subscriptions.CalendarSubscription
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [CalendarEvent::class, CalendarSubscription::class],
    version = 2,
    exportSchema = false
)
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
                ).addMigrations(MIGRATION_1_2)
                    .build()

                INSTANCE = instance
                instance
            }
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE events ADD COLUMN reminderMinutes INTEGER")
            }
        }
    }
}
