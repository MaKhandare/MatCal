package com.itsmatok.matcal.data.calendar.events

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Entity(tableName = "events")
data class CalendarEvent(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val date: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val title: String,
    val location: String? = null,
    val description: String? = null,
    val source: String? = null,

    val sourceUrl: String? = null,
    val iCalUid: String? = null
)


class Converters {
    @TypeConverter
    fun fromLocalDate(date: LocalDate): String {
        return date.format(DateTimeFormatter.ISO_LOCAL_DATE)
    }

    @TypeConverter
    fun toLocalDate(dateString: String): LocalDate {
        return LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE)
    }

    @TypeConverter
    fun fromLocalTime(time: LocalTime): String {
        return time.format(DateTimeFormatter.ISO_LOCAL_TIME)
    }

    @TypeConverter
    fun toLocalTime(timeString: String): LocalTime {
        return LocalTime.parse(timeString, DateTimeFormatter.ISO_LOCAL_TIME)
    }
}