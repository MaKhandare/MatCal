package com.itsmatok.matcal.data.calendar.subscriptions

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subscriptions")
data class CalendarSubscription(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val url: String,
    val name: String,
    val color: Long? = null, // TODO: add colors for different subscriptions
    val enabled: Boolean = true
)