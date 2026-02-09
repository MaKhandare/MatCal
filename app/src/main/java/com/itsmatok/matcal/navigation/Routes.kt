package com.itsmatok.matcal.navigation

import kotlinx.serialization.Serializable

@Serializable
object Calendar

@Serializable
object License

@Serializable
data class AddEvent(
    val date: String? = null,
    val hour: Int? = null
)

@Serializable
data class EditEvent(val eventId: Int)

@Serializable
data class EventDetails(val eventId: Int)

@Serializable
object ManageCalendars
